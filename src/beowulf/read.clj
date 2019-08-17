(ns beowulf.read
  (:require [clojure.math.numeric-tower :refer [expt]]
            [clojure.string :refer [starts-with? upper-case]]
            [instaparse.core :as i]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL]])
;;  (:import [beowulf.cons-cell ConsCell])
  )

(declare generate)

(def parse
  "Parse a string presented as argument into a parse tree which can then
  be operated upon further."
  (i/parser
    (str
      ;; top level: we accept mexprs as well as sexprs.
      "expr := mexpr | sexpr;"

      ;; mexprs. I'm pretty clear that Lisp 1.5 could never read these,
      ;; but it's a convenience.
      "mexpr := λexpr | fncall | defn | cond | mvar;
      λexpr := λ lsqb bindings semi-colon body rsqb;
      λ := 'λ';
      bindings := lsqb args rsqb;
      body := (expr semi-colon opt-space)* expr;
      fncall := fn-name lsqb args rsqb;
      lsqb := '[';
      rsqb := ']';
      defn := mexpr opt-space '=' opt-space mexpr;
      cond := lsqb (cond-clause semi-colon opt-space)* cond-clause rsqb;
      cond-clause := expr opt-space arrow opt-space expr;
      arrow := '->';
      args := (expr semi-colon opt-space)* expr;
      fn-name := mvar;
      mvar := #'[a-z]+';
      semi-colon := ';';"

      ;; sexprs. Note it's not clear to me whether Lisp 1.5 had the quote macro,
      ;; but I've included it on the basis that it can do little harm.
      "sexpr := quoted-expr | atom | number | dotted-pair | list;
      list := lpar sexpr rpar | lpar (sexpr sep)* rpar | lpar (sexpr sep)* dot-terminal;
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

(defn simplify
  "Simplify this parse tree `p`. If `p` is an instaparse failure object, throw
  an `ex-info`, with `p` as the value of its `:failure` key."
  ([p]
   (if
     (instance? instaparse.gll.Failure p)
     (throw (ex-info "Ic ne behæfd" {:cause :parse-failure :failure p}))
     (simplify p :sexpr)))
  ([p context]
  (if
    (coll? p)
    (apply
      vector
      (remove
        #(if (coll? %) (empty? %))
        (case (first p)
          (:arg :expr :coefficient :fn-name :number :sexpr) (simplify (second p) context)
          (:λexpr
            :args :bindings :body :cond :cond-clause :dot-terminal
            :fncall :octal :quoted-expr :scientific) (map #(simplify % context) p)
          (:arrow :dot :e :lpar :lsqb :opt-space :q :quote :rpar :rsqb
            :semi-colon :sep :space) nil
          :atom (if
                  (= context :mexpr)
                  [:quoted-expr p]
                  p)
          :dotted-pair (if
                         (= context :mexpr)
                         [:fncall
                          [:mvar "cons"]
                          [:args
                           (simplify (nth p 1) context)
                           (simplify (nth p 2) context)]]
                         (map simplify p))
          :mexpr (simplify (second p) :mexpr)
          :list (if
                  (= context :mexpr)
                  [:fncall
                   [:mvar "list"]
                   [:args (apply vector (map simplify (rest p)))]]
                  (map #(simplify % context) p))
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
  (if
    (and (coll? p)(= :cond-clause (first p)))
    (make-beowulf-list
      (list (generate (nth p 1))
                     (generate (nth p 2))))))

(defn gen-cond
  "Generate a cond statement from this simplified parse tree fragment `p`;
  returns `nil` if `p` does not represent a (MEXPR) cond statement."
  [p]
  (if
    (and (coll? p)(= :cond (first p)))
    (make-beowulf-list
      (cons
        'COND
        (map
          gen-cond-clause
          (rest p))))))

(defn gen-fn-call
  "Generate a function call from this simplified parse tree fragment `p`;
  returns `nil` if `p` does not represent a (MEXPR) function call.
  TODO: I'm not yet certain but it appears that args in mexprs are
  implicitly quoted; this function does not (yet) do that."
  [p]
  (if
    (and (coll? p)(= :fncall (first p))(= :mvar (first (second p))))
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
       (\+ \-)(strip-leading-zeros (subs s 1) (str (first s) prefix))
       "0" (strip-leading-zeros (subs s 1) prefix)
       (str prefix s)))))

(defn generate
  "Generate lisp structure from this parse tree `p`. It is assumed that
  `p` has been simplified."
  [p]
  (if
    (coll? p)
    (case (first p)
      :λ "LAMBDA"
      :λexpr (make-cons-cell
               (generate (nth p 1))
               (make-cons-cell (generate (nth p 2))
                               (generate (nth p 3))))
      (:args :list) (gen-dot-terminated-list (rest p))
      :atom (symbol (second p))
      :bindings (generate (second p))
      :body (make-beowulf-list (map generate (rest p)))
      :cond (gen-cond p)
      (:decimal :integer) (read-string (strip-leading-zeros (second p)))
      :dotted-pair (make-cons-cell
                     (generate (nth p 1))
                     (generate (nth p 2)))
      :exponent (generate (second p))
      :fncall (gen-fn-call p)
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
      (throw (Exception. (str "Cannot yet generate " (first p)))))
    p))

(defmacro gsp
  "Shortcut macro - the internals of read; or, if you like, read-string.
  Argument `s` should be a string representation of a valid Lisp
  expression."
  [s]
  `(generate (simplify (parse ~s))))

(defn READ
  [input]
  (gsp (or input (read-line))))
