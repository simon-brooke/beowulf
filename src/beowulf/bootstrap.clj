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
            [beowulf.cons-cell :refer [CAR CDR CONS LIST make-beowulf-list make-cons-cell
                                       pretty-print T F]]
            [beowulf.host :refer [ADD1 DIFFERENCE FIXP NUMBERP PLUS QUOTIENT
                                  REMAINDER RPLACA RPLACD SUB1 TIMES]]
            [beowulf.io :refer [SYSIN SYSOUT]]
            [beowulf.oblist :refer [*options* oblist NIL]])
  (:import [beowulf.cons_cell ConsCell]
           [clojure.lang Symbol]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; This file is essentially Lisp as defined in Chapter 1 (pages 1-14) of the
;;; Lisp 1.5 Programmer's Manual; that is to say, a very simple Lisp language,
;;; which should, I believe, be sufficient in conjunction with the functions
;;; provided by `beowulf.host`, be sufficient to bootstrap the full Lisp 1.5
;;; interpreter.
;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(declare APPLY EVAL)


(defmacro NULL
  "Returns `T` if and only if the argument `x` is bound to `NIL`; else `F`."
  [x]
  `(if (= ~x NIL) T F))

(defmacro NILP
  "Not part of LISP 1.5: `T` if `o` is `NIL`, else `NIL`."
  [x]
  `(if (= ~x NIL) T NIL))

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

(defn uaf
  "Universal access function; `l` is expected to be an arbitrary LISP list, `path`
  a (clojure) list of the characters `a` and `d`. Intended to make declaring
  all those fiddly `#'c[ad]+r'` functions a bit easier"
  [l path]
  (cond
    (= l NIL) NIL
    (empty? path) l
    :else
    (try
      (case (last path)
        \a (uaf (.first l) (butlast path))
        \d (uaf (.getCdr l) (butlast path))
        (throw (ex-info (str "uaf: unexpected letter in path (only `a` and `d` permitted): " (last path))
                        {:cause  :uaf
                         :detail :unexpected-letter
                         :expr   (last path)})))
      (catch ClassCastException e
        (throw (ex-info
                (str "uaf: Not a LISP list? " (type l))
                {:cause  :uaf
                 :detail :not-a-lisp-list
                 :expr   l}))))))

(defmacro CAAR [x] `(uaf ~x '(\a \a)))
(defmacro CADR [x] `(uaf ~x '(\a \d)))
(defmacro CDDR [x] `(uaf ~x '(\d \d)))
(defmacro CDAR [x] `(uaf ~x '(\d \a)))

(defmacro CAAAR [x] `(uaf ~x '(\a \a \a)))
(defmacro CAADR [x] `(uaf ~x '(\a \a \d)))
(defmacro CADAR [x] `(uaf ~x '(\a \d \a)))
(defmacro CADDR [x] `(uaf ~x '(\a \d \d)))
(defmacro CDDAR [x] `(uaf ~x '(\d \d \a)))
(defmacro CDDDR [x] `(uaf ~x '(\d \d \d)))
(defmacro CDAAR [x] `(uaf ~x '(\d \a \a)))
(defmacro CDADR [x] `(uaf ~x '(\d \a \d)))

(defmacro CAAAAR [x] `(uaf ~x '(\a \a \a \a)))
(defmacro CAADAR [x] `(uaf ~x '(\a \a \d \a)))
(defmacro CADAAR [x] `(uaf ~x '(\a \d \a \a)))
(defmacro CADDAR [x] `(uaf ~x '(\a \d \d \a)))
(defmacro CDDAAR [x] `(uaf ~x '(\d \d \a \a)))
(defmacro CDDDAR [x] `(uaf ~x '(\d \d \d \a)))
(defmacro CDAAAR [x] `(uaf ~x '(\d \a \a \a)))
(defmacro CDADAR [x] `(uaf ~x '(\d \a \d \a)))
(defmacro CAAADR [x] `(uaf ~x '(\a \a \a \d)))
(defmacro CAADDR [x] `(uaf ~x '(\a \a \d \d)))
(defmacro CADADR [x] `(uaf ~x '(\a \d \a \d)))
(defmacro CADDDR [x] `(uaf ~x '(\a \d \d \d)))
(defmacro CDDADR [x] `(uaf ~x '(\d \d \a \d)))
(defmacro CDDDDR [x] `(uaf ~x '(\d \d \d \d)))
(defmacro CDAADR [x] `(uaf ~x '(\d \a \a \d)))
(defmacro CDADDR [x] `(uaf ~x '(\d \a \d \d)))

(defn EQ
  "Returns `T` if and only if both `x` and `y` are bound to the same atom,
  else `NIL`."
  [x y]
  (cond (and (instance? ConsCell x)
             (.equals x y)) T
        (and (= (ATOM x) T) (= x y)) T
        :else NIL))

(defn EQUAL
  "This is a predicate that is true if its two arguments are identical
  S-expressions, and false if they are different. (The elementary predicate
  `EQ` is defined only for atomic arguments.) The definition of `EQUAL` is
  an example of a conditional expression inside a conditional expression.

  NOTE: returns `F` on failure, not `NIL`"
  [x y]
  (cond
    (= (ATOM x) T) (if (= x y) T F)
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

(defn interop-interpret-q-name
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

(defn to-beowulf
  "Return a beowulf-native representation of the Clojure object `o`.
  Numbers and symbols are unaffected. Collections have to be converted;
  strings must be converted to symbols."
  [o]
  (cond
    (coll? o) (make-beowulf-list o)
    (string? o) (symbol (s/upper-case o))
    :else o))

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

(defn INTEROP
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
  (if-not (:strict *options*)
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
      args'  (to-clojure args)]
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
                   :result result})))))
    (throw
     (ex-info
      (str "INTEROP not allowed in strict mode.")
      {:cause  :interop
       :detail :strict}))))

(defn OBLIST
  "Not certain whether or not this is part of LISP 1.5; adapted from PSL. 
  return the current value of the object list. Note that in PSL this function
  returns a list of the symbols bound, not the whole association list."
  []
  (make-beowulf-list (map CAR @oblist)))

(defn DEFINE
  "Bootstrap-only version of `DEFINE` which, post boostrap, can be overwritten 
  in LISP. 

  The single argument to `DEFINE` should be an assoc list which should be 
  nconc'ed onto the front of the oblist. Broadly, 
  (SETQ OBLIST (NCONC ARG1 OBLIST))"
  [args]
  (swap!
   oblist
   (fn [ob arg1]
     (loop [cursor arg1 a arg1]
       (if (= (CDR cursor) NIL)
         (do
           (.rplacd cursor @oblist)
           (pretty-print a)
           a)
         (recur (CDR cursor) a))))
   (CAR args)))

(defn SET
  "Implementation of SET in Clojure. Add to the `oblist` a binding of the
   value of `var` to the value of `val`. NOTE WELL: this is not SETQ!"
  [symbol val]
  (when
   (swap!
    oblist
    (fn [ob s v] (make-cons-cell (make-cons-cell s v) ob))
    symbol val)
    NIL))

(defn- apply-symbolic
  "Apply this `funtion-symbol` to these `args` in this `environment` and 
   return the result."
  [^Symbol function-symbol ^ConsCell args ^ConsCell environment]
  (let [fn (try (EVAL function-symbol environment)
                (catch Throwable any (when (:trace *options*)
                                       (println any))))]
    (if (and fn (not= fn NIL))
      (APPLY fn args environment)
      (case function-symbol ;; there must be a better way of doing this!
        ADD1 (apply ADD1 args)
        APPEND (apply APPEND args)
        APPLY (apply APPLY args)
        ATOM (ATOM? (CAR args))
        CAR (CAAR args)
        CDR (CDAR args)
        CONS (make-cons-cell (CAR args) (CADR args))
        DEFINE (DEFINE (CAR args))
        DIFFERENCE (DIFFERENCE (CAR args) (CADR args))
        EQ (apply EQ args)
        ;; think about EVAL. Getting the environment right is subtle
        FIXP (apply FIXP args)
        INTEROP (apply INTEROP args)
        NUMBERP (apply NUMBERP args)
        PLUS (apply PLUS args)
        PRETTY (apply pretty-print args)
        QUOTIENT (apply QUOTIENT args)
        REMAINDER (apply REMAINDER args)
        RPLACA (apply RPLACA args)
        RPLACD (apply RPLACD args)
        SET (apply SET args)
        SYSIN (apply SYSIN args)
        SYSOUT (apply SYSOUT args)
        TIMES (apply TIMES args)
        ;; else
        (ex-info "No function found"
                 {:context "APPLY"
                  :function function-symbol
                  :args args})))))

(defn apply-internal
  "Internal guts of both `APPLY` and `traced-apply`. Apply this `function` to 
   these `arguments` in this `environment` and return the result.
   
   For bootstrapping, at least, a version of APPLY written in Clojure.
   All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
   See page 13 of the Lisp 1.5 Programmers Manual."
  [function args environment]
  (cond
    (= NIL function) (if (:strict *options*)
                       NIL
                       (throw (ex-info "NIL is not a function"
                                       {:context "APPLY"
                                        :function "NIL"
                                        :args args})))
    (= (ATOM? function) T) (apply-symbolic function args environment)
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

(deftrace traced-apply
  "Traced wrapper for `internal-apply`, q.v. Apply this `function` to 
   these `arguments` in this `environment` and return the result."
  [function args environment]
  (apply-internal function args environment))

(defn APPLY
  "Despatcher for APPLY, selects beteen `traced-apply` and `apply-internal`
   based on the value of `:trace` in `*options*`. Apply this `function` to 
   these `arguments` and return the result. If `environment` is not passed,
   if defaults to the current value of the global object list."
  ([function args]
   (APPLY function args @oblist))
  ([function args environment]
   (if
    (:trace *options*)
     (traced-apply function args environment)
     (apply-internal function args environment))))

(defn- EVCON
  "Inner guts of primitive COND. All `clauses` are assumed to be
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

(defn- eval-symbolic [^Symbol s env]
  (let [binding (CDR (ASSOC s env))]
    (if (= binding NIL)
      (throw (ex-info (format "No binding for symbol `%s`" s)
                      {:phase :eval
                       :symbol s}))
      binding)))

(defn- eval-internal
  "Common guts for both EVAL and traced-eval. Evaluate this `expr`
   and return the result. 
   
   For bootstrapping, at least, this is a version of EVAL written in Clojure.
   All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
   See page 13 of the Lisp 1.5 Programmers Manual."
  [expr env]
  (cond
    (= (NUMBERP expr) T) expr
    (symbol? expr) (eval-symbolic expr env)
    (string? expr) (if (:strict *options*)
                     (throw
                      (ex-info
                       (str "EVAL: strings not allowed in strict mode: \"" expr "\"")
                       {:phase  :eval
                        :detail :strict
                        :expr   expr}))
                     (symbol expr))
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

(deftrace traced-eval
  "Essentially, identical to EVAL except traced."
  [expr env]
  (eval-internal expr env))

;; (defmacro EVAL
;;   "For bootstrapping, at least, a version of EVAL written in Clojure.
;;   All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
;;   See page 13 of the Lisp 1.5 Programmers Manual."
;;   [expr env]
;;   `(if
;;    (:trace *options*)
;;     (traced-eval ~expr ~env)
;;     (eval-internal ~expr ~env)))


(defn EVAL
  "Despatcher for EVAL, selects beteen `traced-eval` and `eval-internal`
   based on the value of `:trace` in `*options*`. Evaluate this `expr`
   and return the result. If `environment` is not passed,
   if defaults to the current value of the global object list.
   All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects."
  ([expr]
   (EVAL expr @oblist))
  ([expr env]
   (if
    (:trace *options*)
     (traced-eval expr env)
     (eval-internal expr env))))

