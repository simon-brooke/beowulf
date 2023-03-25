(ns beowulf.read
  "This provides the reader required for boostrapping. It's not a bad
  reader - it provides feedback on errors found in the input - but it isn't
  the real Lisp reader.

  Intended deviations from the behaviour of the real Lisp reader are as follows:

  1. It reads the meta-expression language `MEXPR` in addition to fLAMthe
      symbolic expression language `SEXPR`, which I do not believe the Lisp 1.5
      reader ever did;
  2. It treats everything between a semi-colon and an end of line as a comment,
      as most modern Lisps do; but I do not believe Lisp 1.5 had this feature.

  Both these extensions can be disabled by using the `--strict` command line
  switch."
  (:require [beowulf.bootstrap :refer [*options*]]
            [clojure.math.numeric-tower :refer [expt]]
            [clojure.string :refer [join split starts-with? trim upper-case]]
            [instaparse.core :as i]
            [instaparse.failure :as f]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL]])
  (:import [java.io InputStream]
           [instaparse.gll Failure]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; This file provides the reader required for boostrapping. It's not a bad
;;; reader - it provides feedback on errors found in the input - but it isn't
;;; the real Lisp reader.
;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(declare generate)

(defn strip-line-comments
  "Strip blank lines and comment lines from this string `s`, expected to
   be Lisp source."
  [^String s]
  (join "\n"
        (remove
         #(or (empty? %)
              (starts-with? (trim %) ";;"))
         (split s #"\n"))))

(defn number-lines
  ([^String s]
   (number-lines s nil))
  ([^String s ^Failure e]
   (let [l (-> e :line)
         c (-> e :column)]
     (join "\n"
           (map #(str (format "%5d %s" (inc %1) %2)
                      (when (= l (inc %1))
                        (str "\n" (apply str (repeat c " ")) "^")))
                (range)
                (split s #"\n"))))))

(def parse
  "Parse a string presented as argument into a parse tree which can then
  be operated upon further."
  (i/parser
   (str
    ;; we tolerate whitespace and comments around legitimate input
    "raw := expr | opt-comment expr opt-comment;"
        ;; top level: we accept mexprs as well as sexprs.
    "expr := mexpr | sexpr ;"

      ;; comments. I'm pretty confident Lisp 1.5 did NOT have these.
    "comment := opt-space <';;'> opt-space #'[^\\n\\r]*';"

     ;; there's a notation comprising a left brace followed by mexprs
     ;; followed by a right brace which doesn't seem to be documented 
     ;; but I think must represent a prog(?)

    ;; "prog := lbrace exprs rbrace;"
      ;; mexprs. I'm pretty clear that Lisp 1.5 could never read these,
      ;; but it's a convenience.

    "exprs := expr | exprs;"
    "mexpr := λexpr | fncall | defn | cond | mvar | iexpr | mexpr comment;
      λexpr := λ lsqb bindings semi-colon body rsqb;
      λ := 'λ';
      bindings := lsqb args rsqb;
      body := (expr semi-colon opt-space)* expr;
      fncall := fn-name lsqb args rsqb;
      lsqb := '[';
      rsqb := ']';
       lbrace := '{';
       rbrace := '}';
      defn := mexpr opt-space '=' opt-space mexpr;
      cond := lsqb (opt-space cond-clause semi-colon opt-space)* cond-clause rsqb;
      cond-clause := expr opt-space arrow opt-space expr opt-space;
      arrow := '->';
      args := (opt-space expr semi-colon opt-space)* expr;
      fn-name := mvar;
      mvar := #'[a-z]+';
      semi-colon := ';';"

    ;; Infix operators appear in mexprs, e.g. on page 7. Ooops!
    ;; I do not know what infix operators are considered legal.
    "iexpr := iexp iop iexp;
     iexp := mexpr | mvar | number | mexpr | opt-space iexp opt-space;
    iop := '>' | '<' | '+' | '-' | '/' | '=' ;"

      ;; comments. I'm pretty confident Lisp 1.5 did NOT have these.
    "opt-comment := opt-space | comment;"
    "comment := opt-space <';;'> #'[^\\n\\r]*' opt-space;"

      ;; sexprs. Note it's not clear to me whether Lisp 1.5 had the quote macro,
      ;; but I've included it on the basis that it can do little harm.
    "sexpr := quoted-expr | atom | number | dotted-pair | list | sexpr comment;
      list := lpar sexpr rpar | lpar (sexpr sep)* rpar | lpar (sexpr sep)* dot-terminal | lbrace exprs rbrace;
      list := lpar opt-space sexpr rpar | lpar opt-space (sexpr sep)* rpar | lpar opt-space (sexpr sep)* dot-terminal;
      dotted-pair := lpar dot-terminal ;
      dot := '.';
      lpar := '(';
      rpar := ')';
      quoted-expr := quote sexpr;
      quote := '\\'';
      dot-terminal := sexpr space dot space sexpr rpar;
      space := #'\\p{javaWhitespace}+';
      opt-space := #'\\p{javaWhitespace}*';
      sep := ',' | opt-space;
      atom := #'[A-Z][A-Z0-9]*';"

      ;; Lisp 1.5 supported octal as well as decimal and scientific notation
    "number := integer | decimal | scientific | octal;
      integer := #'-?[1-9][0-9]*';
      decimal := #'-?[1-9][0-9]*\\.?[0-9]*' | #'0.[0-9]*';
      scientific := coefficient e exponent;
      coefficient := decimal;
      exponent := integer;
      e := 'E';
      octal := #'[+-]?[0-7]+{1,12}' q scale-factor;
      q := 'Q';
      scale-factor := #'[0-9]*'")))

(declare simplify)

(defn simplify-second-of-two
  "There are a number of possible simplifications such that if the `tree` has
  only two elements, the second is semantically sufficient."
  [tree context]
  (if (= (count tree) 2)
    (simplify (second tree) context)
    tree))

(defn remove-optional-space
  [tree]
  (if (vector? tree)
    (if (= :opt-space (first tree))
      nil
      (remove nil?
              (map remove-optional-space tree)))
    tree))

(defn remove-nesting
  [tree]
  (let [tree' (remove-optional-space tree)]
    (if-let [key (when (and (vector? tree') (keyword? (first tree'))) (first tree'))]
    (loop [r tree']
      (if (and r (vector? r) (keyword? (first r)))
        (if (= (first r) key)
          (recur (simplify (second r) :foo))
          r)
        r))
    tree')))


(defn simplify
  "Simplify this parse tree `p`. If `p` is an instaparse failure object, throw
  an `ex-info`, with `p` as the value of its `:failure` key."
  ([p]
   (if
    (instance? Failure p)
     (throw (ex-info (str "Ic ne behæfd: " (f/pprint-failure p)) {:cause   :parse-failure
                                                                  :phase   :simplify
                                                                  :failure p}))
     (simplify p :expr)))
  ([p context]
   (if
    (coll? p)
     (apply
      vector
      (remove
       #(when (coll? %) (empty? %))
       (case (first p)
         (:λexpr
          :args :bindings :body :cond :cond-clause :defn :dot-terminal
          :fncall :lhs :octal :quoted-expr :rhs :scientific) (map #(simplify % context) p)
         (:arg :expr :coefficient :fn-name :number) (simplify (second p) context)
         (:arrow :dot :e :lpar :lsqb  :opt-comment :opt-space :q :quote :rpar :rsqb
                 :semi-colon :sep :space) nil
         :atom (if
                (= context :mexpr)
                 [:quoted-expr p]
                 p)
         :comment (when
                   (:strict *options*)
                    (throw
                     (ex-info "Cannot parse comments in strict mode"
                              {:cause :strict})))
         :dotted-pair (if
                       (= context :mexpr)
                        [:fncall
                         [:mvar "cons"]
                         [:args
                          (simplify (nth p 1) context)
                          (simplify (nth p 2) context)]]
                        (map simplify p))
         :iexp (second (remove-nesting p))
         :iexpr [:iexpr
                 [:lhs (simplify (second p) context)]
                 (simplify (nth p 2) context) ;; really should be the operator
                 [:rhs (simplify (nth p 3) context)]]
         :mexpr (if
                 (:strict *options*)
                  (throw
                   (ex-info "Cannot parse meta expressions in strict mode"
                            {:cause :strict}))
                  (simplify (second p) :mexpr))
         :list (if
                (= context :mexpr)
                 [:fncall
                  [:mvar "list"]
                  [:args (apply vector (map simplify (rest p)))]]
                 (map #(simplify % context) p))
         :raw (first (remove empty? (map simplify (rest p))))
         :sexpr (simplify (second p) :sexpr)
          ;;default
         p)))
     p)))


;; # From Lisp 1.5 Programmers Manual, page 10
;; Note that I've retyped much of this, since copy/pasting out of PDF is less
;; than reliable. Any typos are mine. Quote starts [[

;; We are now in a position to define the universal LISP function
;; evalquote[fn;args], When evalquote is given a function and a list of arguments
;; for that function, it computes the value of the function applied to the arguments.
;; LISP functions have S-expressions as arguments. In particular, the argument "fn"
;; of the function evalquote must be an S-expression. Since we have been
;; writing functions as M-expressions, it is necessary to translate them into
;; S-expressions.

;; The following rules define a method of translating functions written in the
;; meta-language into S-expressions.
;; 1. If the function is represented by its name, it is translated by changing
;;    all of the letters to upper case, making it an atomic symbol. Thus is
;;    translated to CAR.
;; 2. If the function uses the lambda notation, then the expression
;;    λ[[x ..;xn]; ε] is translated into (LAMBDA (X1 ...XN) ε*), where ε* is the translation
;;    of ε.
;; 3. If the function begins with label, then the translation of
;;    label[α;ε] is (LABEL α* ε*).

;; Forms are translated as follows:
;; 1. A variable, like a function name, is translated by using uppercase letters.
;;    Thus the translation of varl is VAR1.
;; 2. The obvious translation of letting a constant translate into itself will not
;;    work. Since the translation of x is X, the translation of X must be something
;;    else to avoid ambiguity. The solution is to quote it. Thus X is translated
;;    into (QUOTE X).
;; 3. The form fn[argl;. ..;argn] is translated into (fn* argl* ...argn*)
;; 4. The conditional expression [pl-el;...;pn-en] is translated into
;;    (COND (p1* e1*)...(pn* en*))

;; ## Examples

;; M-expressions                                S-expressions
;; x                                            X
;; car                                          CAR
;; car[x]                                       (CAR X)
;; T                                            (QUOTE T)
;; ff[car [x]]                                  (FF (CAR X))
;; [atom[x]->x; T->ff[car[x]]]                  (COND ((ATOM X) X)
;;                                                ((QUOTE T)(FF (CAR X))))
;; label[ff;λ[[x];[atom[x]->x; T->ff[car[x]]]]] (LABEL FF (LAMBDA (X) (COND
;;                                                ((ATOM X) X)
;;                                                ((QUOTE T)(FF (CAR X))))))

;; ]] quote ends

(defn gen-cond-clause
  "Generate a cond clause from this simplified parse tree fragment `p`;
  returns `nil` if `p` does not represent a cond clause."
  [p]
  (when
   (and (coll? p) (= :cond-clause (first p)))
    (make-beowulf-list
     (list (if (= (nth p 1) [:quoted-expr [:atom "T"]])
             'T
             (generate (nth p 1)))
           (generate (nth p 2))))))

(defn gen-cond
  "Generate a cond statement from this simplified parse tree fragment `p`;
  returns `nil` if `p` does not represent a (MEXPR) cond statement."
  [p]
  (when
   (and (coll? p) (= :cond (first p)))
    (make-beowulf-list
     (cons
      'COND
      (map
       generate
       (rest p))))))

(defn gen-fn-call
  "Generate a function call from this simplified parse tree fragment `p`;
  returns `nil` if `p` does not represent a (MEXPR) function call."
  [p]
  (when
   (and (coll? p) (= :fncall (first p)) (= :mvar (first (second p))))
    (make-cons-cell
     (generate (second p))
     (generate (nth p 2)))))


(defn gen-dot-terminated-list
  "Generate a list, which may be dot-terminated, from this partial parse tree
  'p'. Note that the function acts recursively and progressively decapitates
  its argument, so that the argument will not always be a valid parse tree."
  [p]
  (cond
    (empty? p)
    NIL
    (and (coll? (first p)) (= :dot-terminal (first (first p))))
    (let [dt (first p)]
      (make-cons-cell
       (generate (nth dt 1))
       (generate (nth dt 2))))
    :else
    (make-cons-cell
     (generate (first p))
     (gen-dot-terminated-list (rest p)))))

(defn generate-defn
  [tree]
  (make-beowulf-list
   (list 'SET
         (list 'QUOTE (generate (-> tree second second)))
         (list 'QUOTE
               (cons 'LAMBDA
                     (cons (generate (nth (second tree) 2))
                           (map generate (-> tree rest rest rest))))))))

(defn generate-set
  "Actually not sure what the mexpr representation of set looks like"
  [tree]
  (throw (ex-info "Not Yet Implemented" {:feature "generate-set"})))

(defn generate-assign
  "Generate an assignment statement based on this `tree`. If the thing 
   being assigned to is a function signature, then we have to do something 
   different to if it's an atom."
  [tree]
  (case (first (second tree))
    :fncall (generate-defn tree)
    (:mvar :atom) (generate-set tree)))

(defn strip-leading-zeros
  "`read-string` interprets strings with leading zeros as octal; strip
  any from this string `s`. If what's left is empty (i.e. there were
  only zeros, return `\"0\"`."
  ([s]
   (strip-leading-zeros s ""))
  ([s prefix]
   (if
    (empty? s) "0"
    (case (first s)
      (\+ \-) (strip-leading-zeros (subs s 1) (str (first s) prefix))
      "0" (strip-leading-zeros (subs s 1) prefix)
      (str prefix s)))))

(defn generate
  "Generate lisp structure from this parse tree `p`. It is assumed that
  `p` has been simplified."
  [p]
  (try
    (if
     (coll? p)
      (case (first p)
        :λ "LAMBDA"
        :λexpr (make-cons-cell
                (generate (nth p 1))
                (make-cons-cell (generate (nth p 2))
                                (generate (nth p 3))))
        :args (make-beowulf-list (map generate (rest p)))
        :atom (symbol (second p))
        :bindings (generate (second p))
        :body (make-beowulf-list (map generate (rest p)))
        :cond (gen-cond p)
        :cond-clause (gen-cond-clause p)
        (:decimal :integer) (read-string (strip-leading-zeros (second p)))
        :defn (generate-assign p)
        :dotted-pair (make-cons-cell
                      (generate (nth p 1))
                      (generate (nth p 2)))
        :exponent (generate (second p))
        :fncall (gen-fn-call p)
        :list (gen-dot-terminated-list (rest p))
        :mvar (symbol (upper-case (second p)))
        :octal (let [n (read-string (strip-leading-zeros (second p) "0"))
                     scale (generate (nth p 2))]
                 (* n (expt 8 scale)))

      ;; the quote read macro (which probably didn't exist in Lisp 1.5, but...)
        :quoted-expr (make-beowulf-list (list 'QUOTE (generate (second p))))
        :scale-factor (if
                       (empty? (second p)) 0
                       (read-string (strip-leading-zeros (second p))))
        :scientific (let [n (generate (second p))
                          exponent (generate (nth p 2))]
                      (* n (expt 10 exponent)))

      ;; default
        (throw (ex-info (str "Unrecognised head: " (first p))
                        {:generating p})))
      p)
    (catch Throwable any
      (throw (ex-info "Could not generate"
                      {:generating p}
                      any)))))

;; (defn parse
;;   "Parse string `s` into a parse tree which can then be operated upon further."
;;   [s]
;;   (let [r (parse-internal s)]
;;     (when (instance? Failure r)
;;       (throw
;;        (ex-info "Parse failed"
;;                 (merge {:fail r :source s} r))))
;;     r))


(defn gsp
  "Shortcut macro - the internals of read; or, if you like, read-string.
  Argument `s` should be a string representation of a valid Lisp
  expression."
  [s]
  (let [source (strip-line-comments s)
        parse-tree (parse source)]
    (if (instance? Failure parse-tree)
      (doall (println (number-lines source parse-tree))
             (throw (ex-info "Parse failed" (assoc parse-tree :source source))))
      (generate (simplify parse-tree)))))

(defn READ
  "An implementation of a Lisp reader sufficient for bootstrapping; not necessarily
  the final Lisp reader. `input` should be either a string representation of a LISP
  expression, or else an input stream. A single form will be read."
  ([]
   (gsp (read-line)))
  ([input]
   (cond
     (empty? input) (gsp (read-line))
     (string? input) (gsp input)
     (instance? InputStream input) (READ (slurp input))
     :else    (throw (ex-info "READ: `input` should be a string or an input stream" {})))))
