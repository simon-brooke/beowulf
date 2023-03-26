(ns beowulf.reader.generate
  (:require [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL]]
            [clojure.math.numeric-tower :refer [expt]]
            [clojure.string :refer [upper-case]]))

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

(declare generate)

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
