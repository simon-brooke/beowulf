(ns beowulf.bootstrap
  "Lisp as defined in Chapter 1 (pages 1-14) of the
  `Lisp 1.5 Programmer's Manual`; that is to say, a very simple Lisp language,
  which should, I believe, be sufficient in conjunction with the functions
  provided by `beowulf.host`, be sufficient to bootstrap the full Lisp 1.5
  interpreter..

  The convention is adopted that functions in this file with names in
  ALLUPPERCASE are Lisp 1.5 functions (although written in Clojure) and that
  therefore all arguments must be numbers, symbols or `beowulf.cons_cell.ConsCell`
  objects."
  (:require [clojure.string :as s]
            [clojure.tools.trace :refer [deftrace]]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL T F]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; This file is essentially Lisp as defined in Chapter 1 (pages 1-14) of the
;;; Lisp 1.5 Programmer's Manual; that is to say, a very simple Lisp language,
;;; which should, I believe, be sufficient in conjunction with the functions
;;; provided by `beowulf.host`, be sufficient to bootstrap the full Lisp 1.5
;;; interpreter.
;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(declare EVAL)

(def oblist
  "The default environment."
  (atom NIL))

(def ^:dynamic *options*
  "Command line options from invocation."
  {})

(defmacro NULL
  "Returns `T` if and only if the argument `x` is bound to `NIL`; else `F`."
  [x]
  `(if (= ~x NIL) T F))

(defmacro ATOM
  "Returns `T` if and only if the argument `x` is bound to an atom; else `F`.
  It is not clear to me from the documentation whether `(ATOM 7)` should return
  `T` or `F`. I'm going to assume `T`."
  [x]
  `(if (or (symbol? ~x) (number? ~x)) T F))

(defmacro ATOM?
  "The convention of returning `F` from predicates, rather than `NIL`, is going
  to tie me in knots. This is a variant of `ATOM` which returns `NIL`
  on failure."
  [x]
  `(if (or (symbol? ~x) (number? ~x)) T NIL))

(defmacro NUMBERP
  "Returns `T` if and only if the argument `x` is bound to an number; else `F`.
  TODO: check whether floating point numbers, rationals, etc were numbers in Lisp 1.5"
  [x]
  `(if (number? ~x) T F))

(defn CAR
  "Return the item indicated by the first pointer of a pair. NIL is treated
  specially: the CAR of NIL is NIL."
  [x]
  (cond
    (= x NIL) NIL
    (instance? beowulf.cons_cell.ConsCell x) (.first x)
    :else
    (throw
      (Exception.
        (str "Cannot take CAR of `" x "` (" (.getName (.getClass x)) ")")))))

(defn CDR
  "Return the item indicated by the second pointer of a pair. NIL is treated
  specially: the CDR of NIL is NIL."
  [x]
  (cond
    (= x NIL) NIL
    (instance? beowulf.cons_cell.ConsCell x) (.getCdr x)
    :else
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
            \a (uaf (.first l) (butlast path))
            \d (uaf (.getCdr l) (butlast path)))))

(defn CAAR [x] (uaf x (seq "aa")))
(defn CADR [x] (uaf x (seq "ad")))
(defn CDDR [x] (uaf x (seq "dd")))
(defn CDAR [x] (uaf x (seq "da")))

(defn CAAAR [x] (uaf x (seq "aaa")))
(defn CAADR [x] (uaf x (seq "aad")))
(defn CADAR [x] (uaf x (seq "ada")))
(defn CADDR [x] (uaf x (seq "add")))
(defn CDDAR [x] (uaf x (seq "dda")))
(defn CDDDR [x] (uaf x (seq "ddd")))
(defn CDAAR [x] (uaf x (seq "daa")))
(defn CDADR [x] (uaf x (seq "dad")))

(defn CAAAAR [x] (uaf x (seq "aaaa")))
(defn CAADAR [x] (uaf x (seq "aada")))
(defn CADAAR [x] (uaf x (seq "adaa")))
(defn CADDAR [x] (uaf x (seq "adda")))
(defn CDDAAR [x] (uaf x (seq "ddaa")))
(defn CDDDAR [x] (uaf x (seq "ddda")))
(defn CDAAAR [x] (uaf x (seq "daaa")))
(defn CDADAR [x] (uaf x (seq "dada")))
(defn CAAADR [x] (uaf x (seq "aaad")))
(defn CAADDR [x] (uaf x (seq "aadd")))
(defn CADADR [x] (uaf x (seq "adad")))
(defn CADDDR [x] (uaf x (seq "addd")))
(defn CDDADR [x] (uaf x (seq "ddad")))
(defn CDDDDR [x] (uaf x (seq "dddd")))
(defn CDAADR [x] (uaf x (seq "daad")))
(defn CDADDR [x] (uaf x (seq "dadd")))

(defn EQ
  "Returns `T` if and only if both `x` and `y` are bound to the same atom,
  else `F`."
  [x y]
  (if (and (= (ATOM x) T) (= x y)) T F))

(defn EQUAL
  "This is a predicate that is true if its two arguments are identical
  S-expressions, and false if they are different. (The elementary predicate
  `EQ` is defined only for atomic arguments.) The definition of `EQUAL` is
  an example of a conditional expression inside a conditional expression.

  NOTE: returns `F` on failure, not `NIL`"
  [x y]
  (cond
    (= (ATOM x) T) (EQ x y)
    (= (EQUAL (CAR x) (CAR y)) T) (EQUAL (CDR x) (CDR y))
    :else F))

(defn SUBST
  "This function gives the result of substituting the S-expression `x` for
  all occurrences of the atomic symbol `y` in the S-expression `z`."
  [x y z]
  (cond
    (= (EQUAL y z) T) x
    (= (ATOM? z) T) z ;; NIL is a symbol
    :else
    (make-cons-cell (SUBST x y (CAR z)) (SUBST x y (CDR z)))))

(defn APPEND
  "Append the the elements of `y` to the elements of `x`.

  All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 11 of the Lisp 1.5 Programmers Manual."
  [x y]
  (cond
    (= x NIL) y
    :else
    (make-cons-cell (CAR x) (APPEND (CDR x) y))))

(defn MEMBER
  "This predicate is true if the S-expression `x` occurs among the elements
  of the list `y`.

  All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
  See page 11 of the Lisp 1.5 Programmers Manual."
  [x y]
  (cond
    (= y NIL) F ;; NOTE: returns F on falsity, not NIL
    (= (EQUAL x (CAR y)) T) T
    :else (MEMBER x (CDR y))))

(defn PAIRLIS
  "This function gives the list of pairs of corresponding elements of the
  lists `x` and `y`, and APPENDs this to the list `a`. The resultant list
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
            (make-cons-cell (CAR x) (CAR y))
            (PAIRLIS (CDR x) (CDR y) a))))

(defmacro QUOTE
  "Quote, but in upper case for LISP 1.5"
  [f]
  `(quote ~f))

(defn ASSOC
  "If a is an association list such as the one formed by PAIRLIS in the above
  example, then assoc will produce the first pair whose first term is x. Thus
  it is a table searching function.

  All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 12 of the Lisp 1.5 Programmers Manual."
  [x a]
  (cond
    (= NIL a) NIL ;; this clause is not present in the original but is added for
    ;; robustness.
    (= (EQUAL (CAAR a) x) T) (CAR a)
    :else
    (ASSOC x (CDR a))))

(defn- SUB2
  "Internal to `SUBLIS`, q.v., which SUBSTitutes into a list from a store.
  ? I think this is doing variable binding in the stack frame?"
  [a z]
  (cond
    (= NIL a) z
    (= (CAAR a) z) (CDAR a) ;; TODO: this looks definitely wrong
    :else
    (SUB2 (CDR a) z)))

(defn SUBLIS
  "Here `a` is assumed to be an association list of the form
  `((ul . vl)...(un . vn))`, where the `u`s are atomic, and `y` is any
  S-expression. What `SUBLIS` does, is to treat the `u`s as variables when
  they occur in `y`, and to SUBSTitute the corresponding `v`s from the pair
  list.

  My interpretation is that this is variable binding in the stack frame.

  All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 12 of the Lisp 1.5 Programmers Manual."
  [a y]
  (cond
    (= (ATOM? y) T) (SUB2 a y)
    :else
    (make-cons-cell (SUBLIS a (CAR y)) (SUBLIS a (CDR y)))))

(deftrace interop-interpret-q-name
  "For interoperation with Clojure, it will often be necessary to pass
  qualified names that are not representable in Lisp 1.5. This function
  takes a sequence in the form `(PART PART PART... NAME)` and returns
  a symbol in the form `PART.PART.PART/NAME`. This symbol will then be
  tried in both that form and lower-cased. Names with hyphens or
  underscores cannot be represented with this scheme."
  [l]
  (if
    (seq? l)
    (symbol
      (s/reverse
        (s/replace-first
          (s/reverse
            (s/join "." (map str l)))
          "."
          "/")))
    l))

(defn to-clojure
  "If l is a `beowulf.cons_cell.ConsCell`, return a Clojure list having the 
  same members in the same order."
  [l]
  (cond
    (not (instance? beowulf.cons_cell.ConsCell l))
    l
    (= (CDR l) NIL)
    (list (to-clojure (CAR l)))
    :else
    (conj (to-clojure (CDR l)) (to-clojure (CAR l)))))

(deftrace INTEROP
  "Clojure (or other host environment) interoperation API. `fn-symbol` is expected
  to be either

  1. a symbol bound in the host environment to a function; or
  2. a sequence (list) of symbols forming a qualified path name bound to a
     function.

  Lower case characters cannot normally be represented in Lisp 1.5, so both the
  upper case and lower case variants of `fn-symbol` will be tried. If the
  function you're looking for has a mixed case name, that is not currently
  accessible.

  `args` is expected to be a Lisp 1.5 list of arguments to be passed to that
  function. Return value must be something acceptable to Lisp 1.5, so either
  a symbol, a number, or a Lisp 1.5 list.

  If `fn-symbol` is not found (even when cast to lower case), or is not a function,
  or the value returned cannot be represented in Lisp 1.5, an exception is thrown
  with `:cause` bound to `:interop` and `:detail` set to a value representing the
  actual problem."
  [fn-symbol args]
  (let
   [q-name (if
            (seq? fn-symbol)
             (interop-interpret-q-name fn-symbol)
             fn-symbol)
    l-name (symbol (s/lower-case q-name))
    f      (cond
             (try
               (fn? (eval l-name))
               (catch java.lang.ClassNotFoundException e nil)) l-name
             (try
               (fn? (eval q-name))
               (catch java.lang.ClassNotFoundException e nil)) q-name
             :else (throw
                    (ex-info
                     (str "INTEROP: unknown function `" fn-symbol "`")
                     {:cause      :interop
                      :detail     :not-found
                      :name       fn-symbol
                      :also-tried l-name})))
    args' (to-clojure args)]
    (print (str "INTEROP: evaluating `" (cons f args') "`"))
    (flush)
    (let [result (eval (conj args' f))] ;; this has the potential to blow up the world
      (println (str "; returning `" result "`"))

      (cond
        (instance? beowulf.cons_cell.ConsCell result) result
        (coll? result) (make-beowulf-list result)
        (symbol? result) result
        (string? result) (symbol result)
        (number? result) result
        :else (throw
               (ex-info
                (str "INTEROP: Cannot return `" result "` to Lisp 1.5.")
                {:cause  :interop
                 :detail :not-representable
                 :result result}))))))

(defn APPLY
  "For bootstrapping, at least, a version of APPLY written in Clojure.
  All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
  See page 13 of the Lisp 1.5 Programmers Manual."
  [function args environment]
  (cond
    (=
      (ATOM? function)
      T)(cond
           ;; TODO: doesn't check whether `function` is bound in the environment;
           ;; we'll need that before we can bootstrap.
           (= function 'CAR) (CAAR args)
           (= function 'CDR) (CDAR args)
           (= function 'CONS) (make-cons-cell (CAR args) (CADR args))
           (= function 'ATOM) (if (ATOM? (CAR args)) T NIL)
           (= function 'EQ) (if (= (CAR args) (CADR args)) T NIL)
           (= function 'INTEROP) (INTEROP (CAR args) (CDR args))
           :else
           (APPLY
             (EVAL function environment)
             args
             environment))
    (= (first function) 'LAMBDA) (EVAL
                                   (CADDR function)
                                   (PAIRLIS (CADR function) args environment))
    (= (first function) 'LABEL) (APPLY
                                  (CADDR function)
                                  args
                                  (make-cons-cell
                                    (make-cons-cell
                                      (CADR function)
                                      (CADDR function))
                                    environment))))

(defn- EVCON
  "Inner guts of primitive COND. All args are assumed to be
  `beowulf.cons-cell/ConsCell` objects.
  See page 13 of the Lisp 1.5 Programmers Manual."
  [clauses env]
  (if
    (not= (EVAL (CAAR clauses) env) NIL)
    (EVAL (CADAR clauses) env)
    (EVCON (CDR clauses) env)))

(defn- EVLIS
  "Map `EVAL` across this list of `args` in the context of this
  `env`ironment.All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 13 of the Lisp 1.5 Programmers Manual."
  [args env]
  (cond
    (= NIL args) NIL
    :else
    (make-cons-cell
      (EVAL (CAR args) env)
      (EVLIS (CDR args) env))))

(deftrace traced-eval
  "Essentially, identical to EVAL except traced."
  [expr env]
  (cond
    (NUMBERP expr) expr
    (=
      (ATOM? expr) T)
    (CDR (ASSOC expr env))
    (=
      (ATOM? (CAR expr))
      T)(cond
           (= (CAR expr) 'QUOTE) (CADR expr)
           (= (CAR expr) 'COND) (EVCON (CDR expr) env)
           :else (APPLY
                   (CAR expr)
                   (EVLIS (CDR expr) env)
                   env))
    :else (APPLY
            (CAR expr)
            (EVLIS (CDR expr) env)
            env)))

(defn EVAL
  "For bootstrapping, at least, a version of EVAL written in Clojure.
  All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
  See page 13 of the Lisp 1.5 Programmers Manual."
  [expr env]
  (cond
    (true? (:trace *options*))
    (traced-eval expr env)
    (NUMBERP expr) expr
    (=
     (ATOM? expr) T)
    (CDR (ASSOC expr env))
    (=
     (ATOM? (CAR expr))
     T) (cond
          (= (CAR expr) 'QUOTE) (CADR expr)
          (= (CAR expr) 'COND) (EVCON (CDR expr) env)
          :else (APPLY
                 (CAR expr)
                 (EVLIS (CDR expr) env)
                 env))
    :else (APPLY
           (CAR expr)
           (EVLIS (CDR expr) env)
           env)))



