(ns beowulf.eval
  (:require [clojure.tools.trace :refer :all]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL T F]]))

(declare primitive-eval)

(def oblist
  "The default environment; modified certainly be `LABEL` (which seems to
  be Lisp 1.5's equivalent of `SETQ`), possibly by other things."
  (atom NIL))

(defn null
  [x]
  (if (= x NIL) 'T 'F))

(defn primitive-atom
  "It is not clear to me from the documentation whether `(ATOM 7)` should return
  `'T` or `'F`. I'm going to assume `'T`."
  [x]
  (if (or (symbol? x) (number? x)) 'T 'F))

(defn primitive-atom?
  "The convention of returning `'F` from predicates, rather than `NIL`, is going
  to tie me in knots. This is a variant of `primitive-atom` which returns `NIL`
  on failure."
  [x]
  (if (or (symbol? x) (number? x)) 'T NIL))

(defn car
  [x]
  (if
    (instance? beowulf.cons_cell.ConsCell x)
    (.CAR x)
    (throw
      (Exception.
        (str "Cannot take CAR of `" x "` (" (.getName (.getClass x)) ")")))))

(defn cdr
  [x]
  (if
    (instance? beowulf.cons_cell.ConsCell x)
    (.CDR x)
    (throw
      (Exception.
        (str "Cannot take CDR of `" x "` (" (.getName (.getClass x)) ")")))))

(defn uaf
  "Universal access function; `l` is expected to be an arbitrary list, `path`
  a (clojure) list of the characters `a` and `d`. Intended to make declaring
  all those fiddly `#'c[ad]+r'` functions a bit easier"
  [l path]
  (cond
    (= l NIL) NIL
    (empty? path) l
    :else (case (last path)
            \a (uaf (car l) (butlast path))
            \d (uaf (cdr l) (butlast path)))))

(defn caar [x] (uaf x (seq "aa")))
(defn cadr [x] (uaf x (seq "ad")))
(defn cddr [x] (uaf x (seq "dd")))
(defn cdar [x] (uaf x (seq "da")))

(defn caaar [x] (uaf x (seq "aaa")))
(defn caadr [x] (uaf x (seq "aad")))
(defn cadar [x] (uaf x (seq "ada")))
(defn caddr [x] (uaf x (seq "add")))
(defn cddar [x] (uaf x (seq "dda")))
(defn cdddr [x] (uaf x (seq "ddd")))
(defn cdaar [x] (uaf x (seq "daa")))
(defn cdadr [x] (uaf x (seq "dad")))

(defn caaaar [x] (uaf x (seq "aaaa")))
(defn caadar [x] (uaf x (seq "aada")))
(defn cadaar [x] (uaf x (seq "adaa")))
(defn caddar [x] (uaf x (seq "adda")))
(defn cddaar [x] (uaf x (seq "ddaa")))
(defn cdddar [x] (uaf x (seq "ddda")))
(defn cdaaar [x] (uaf x (seq "daaa")))
(defn cdadar [x] (uaf x (seq "dada")))
(defn caaadr [x] (uaf x (seq "aaad")))
(defn caaddr [x] (uaf x (seq "aadd")))
(defn cadadr [x] (uaf x (seq "adad")))
(defn cadddr [x] (uaf x (seq "addd")))
(defn cddadr [x] (uaf x (seq "ddad")))
(defn cddddr [x] (uaf x (seq "dddd")))
(defn cdaadr [x] (uaf x (seq "daad")))
(defn cdaddr [x] (uaf x (seq "dadd")))

(defn eq
  ;; For some reason providing a doc string for this function breaks the
  ;; Clojure parser!
  [x y]
  (if (and (= (primitive-atom x) 'T) (= x y)) 'T 'F))

(defn equal
  "This is a predicate that is true if its two arguments are identical
  S-expressions, and false if they are different. (The elementary predicate
  `eq` is defined only for atomic arguments.) The definition of `equal` is
  an example of a conditional expression inside a conditional expression.

  NOTE: returns F on failure, not NIL"
  [x y]
  (cond
    (= (primitive-atom x) 'T) (eq x y)
    (= (equal (car x) (car y)) 'T) (equal (cdr x) (cdr y))
    :else 'F))

(defn subst
  "This function gives the result of substituting the S-expression `x` for
  all occurrences of the atomic symbol `y` in the S-expression `z`."
  [x y z]
  (cond
    (= (equal y z) 'T) x
    (= (primitive-atom? z) 'T) z ;; NIL is a symbol
    :else
    (make-cons-cell (subst x y (car z)) (subst x y (cdr z)))))

(defn append
  "Append the the elements of `y` to the elements of `x`.

  All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 11 of the Lisp 1.5 Programmers Manual."
  [x y]
  (cond
    (= x NIL) y
    :else
    (make-cons-cell (car x) (append (cdr x) y))))


(defn member
  "This predicate is true if the S-expression `x` occurs among the elements
  of the list `y`.

  All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
  See page 11 of the Lisp 1.5 Programmers Manual."
  [x y]
  (cond
    (= y NIL) F ;; NOTE: returns F on falsity, not NIL
    (= (equal x (car y)) 'T) 'T
    :else (member x (cdr y))))

(defn pairlis
  "This function gives the list of pairs of corresponding elements of the
  lists `x` and `y`, and appends this to the list `a`. The resultant list
  of pairs, which is like a table with two columns, is called an
  association list.

  Eessentially, it builds the environment on the stack, implementing shallow
  binding.

  All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 12 of the Lisp 1.5 Programmers Manual."
  [x y a]
  (cond
    ;; the original tests only x; testing y as well will be a little more
    ;; robust if `x` and `y` are not the same length.
    (or (= NIL x) (= NIL y)) a
    :else (make-cons-cell
            (make-cons-cell (car x) (car y))
            (pairlis (cdr x) (cdr y) a))))

(defn primitive-assoc
  "If a is an association list such as the one formed by pairlis in the above
  example, then assoc will produce the first pair whose first term is x. Thus
  it is a table searching function.

  All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 12 of the Lisp 1.5 Programmers Manual."
  [x a]
  (cond
    (= NIL a) NIL ;; this clause is not present in the original but is added for
    ;; robustness.
    (= (equal (caar a) x) 'T) (car a)
    :else
    (primitive-assoc x (cdr a))))

(defn- sub2
  "Internal to `sublis`, q.v., which substitutes into a list from a store.
  ? I think this is doing variable binding in the stack frame?"
  [a z]
  (cond
    (= NIL a) z
    (= (caar a) z) (cdar a) ;; TODO: this looks definitely wrong
    :else
    (sub2 (cdr a) z)))

(defn sublis
  "Here `a` is assumed to be an association list of the form
  `((ul . vl)...(un . vn))`, where the `u`s are atomic, and `y` is any
  S-expression. What `sublis` does, is to treat the `u`s as variables when
  they occur in `y`, and to substitute the corresponding `v`s from the pair
  list.

  My interpretation is that this is variable binding in the stack frame.

  All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 12 of the Lisp 1.5 Programmers Manual."
  [a y]
  (cond
    (primitive-atom? y) (sub2 a y)
    :else
    (make-cons-cell (sublis a (car y)) (sublis a (cdr y)))))

(deftrace primitive-apply
  "For bootstrapping, at least, a version of APPLY written in Clojure.
  All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
  See page 13 of the Lisp 1.5 Programmers Manual."
  [function args environment]
  (cond
    (primitive-atom? function)(cond
                        (= function 'CAR) (caar args)
                        (= function 'CDR) (cdar args)
                        (= function 'CONS) (make-cons-cell (car args) (cadr args))
                        (= function 'ATOM) (if (primitive-atom? (car args)) T NIL)
                        (= function 'EQ) (if (= (car args) (cadr args)) T NIL)
                        :else
                        (primitive-apply
                          (primitive-eval function environment)
                          args
                          environment))
    (= (first function) 'LAMBDA) (primitive-eval
                                   (caddr function)
                                   (pairlis (cadr function) args environment))
    (= (first function) 'LABEL) (primitive-apply
                                  (caddr function)
                                  args
                                  (make-cons-cell
                                    (make-cons-cell
                                      (cadr function)
                                      (caddr function))
                                    environment))))

(defn- evcon
  "Inner guts of primitive COND. All args are assumed to be
  `beowulf.cons-cell/ConsCell` objects.
  See page 13 of the Lisp 1.5 Programmers Manual."
  [clauses env]
  (if
    (not= (primitive-eval (caar clauses) env) NIL)
    (primitive-eval (cadar clauses) env)
    (evcon (cdr clauses) env)))

(defn- evlis
  "Map `primitive-eval` across this list of `args` in the context of this
  `env`ironment.All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 13 of the Lisp 1.5 Programmers Manual."
  [args env]
  (cond
    (null args) NIL
    :else
    (make-cons-cell
      (primitive-eval (car args) env)
      (evlis (cdr args) env))))


(deftrace primitive-eval
  "For bootstrapping, at least, a version of EVAL written in Clojure.
  All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
  See page 13 of the Lisp 1.5 Programmers Manual."
  [expr env]
  (cond
    (primitive-atom? expr) (cdr (primitive-assoc expr env))
    (primitive-atom? (car expr))(cond
                          (eq (car expr) 'QUOTE) (cadr expr)
                          (eq (car expr) 'COND) (evcon (cdr expr) env)
                          :else (primitive-apply
                                  (car expr)
                                  (evlis (cdr expr) env)
                                  env))
    :else (primitive-apply
            (car expr)
            (evlis (cdr expr) env)
            env)))

