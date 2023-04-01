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
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell
                                       pretty-print T F]]
            [beowulf.host :refer [ADD1 AND ASSOC ATOM ATOM? CAR CDR CONS DEFINE 
                                  DIFFERENCE DOC EQ EQUAL ERROR FIXP GENSYM 
                                  GREATERP lax? LESSP LIST NUMBERP OBLIST
                                  PAIRLIS PLUS QUOTIENT REMAINDER RPLACA RPLACD SET 
                                  TIMES TRACE traced? UNTRACE]]
            [beowulf.io :refer [SYSIN SYSOUT]]
            [beowulf.oblist :refer [*options* oblist NIL]]
            [beowulf.read :refer [READ]])
  (:import [beowulf.cons_cell ConsCell]
           [clojure.lang Symbol]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; Copyright (C) 2022-2023 Simon Brooke
;;;
;;; This program is free software; you can redistribute it and/or
;;; modify it under the terms of the GNU General Public License
;;; as published by the Free Software Foundation; either version 2
;;; of the License, or (at your option) any later version.
;;; 
;;; This program is distributed in the hope that it will be useful,
;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;; GNU General Public License for more details.
;;; 
;;; You should have received a copy of the GNU General Public License
;;; along with this program; if not, write to the Free Software
;;; Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(declare APPLY EVAL)

(defmacro QUOTE
  "Quote, but in upper case for LISP 1.5"
  [f]
  `(quote ~f))

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

;;;; INTEROP feature ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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
                 (catch java.lang.ClassNotFoundException _ nil)) l-name
               (try
                 (fn? (eval q-name))
                 (catch java.lang.ClassNotFoundException _ nil)) q-name
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

(defn- traced-apply
  "Like `APPLY`, but with trace output to console."
  [function-symbol args lisp-fn environment depth]
  (let [indent (apply str (repeat depth "-"))]
    (println (str indent "> " function-symbol " " args))
    (let [r (APPLY lisp-fn args environment depth)]
      (println (str "<" indent " " r))
      r)))

(defn- safe-apply 
  "We've a real problem with varargs functions when `args` is `NIL`, because
   Clojure does not see `NIL` as an empty sequence."
  [clj-fn args]
  (let [args' (when (instance? ConsCell args) args)]
    (apply clj-fn args')))

(defn- apply-symbolic
  "Apply this `funtion-symbol` to these `args` in this `environment` and 
   return the result."
  [^Symbol function-symbol args ^ConsCell environment depth]
  (let [lisp-fn (try (EVAL function-symbol environment depth)
                     (catch Throwable any (when (:trace *options*)
                                            (println any))))]
    (if (and lisp-fn
             (not= lisp-fn NIL)) (if (traced? function-symbol)
                                   (traced-apply function-symbol
                                                 args
                                                 lisp-fn
                                                 environment
                                                 depth)
                                   (APPLY lisp-fn args environment depth))
        (case function-symbol ;; there must be a better way of doing this!
          ADD1 (safe-apply ADD1 args)
          AND (safe-apply AND args)
          APPLY (APPLY (first args) (rest args) environment depth) ;; TODO: need to pass the environment and depth
          ATOM (ATOM? (CAR args))
          CAR (safe-apply CAR args)
          CDR (safe-apply CDR args)
          CONS (safe-apply CONS args)
          DEFINE (DEFINE (CAR args))
          DIFFERENCE (DIFFERENCE (CAR args) (CADR args))
          DOC (DOC (first args))
          EQ (safe-apply EQ args)
          EQUAL (safe-apply EQUAL args)
          ERROR (safe-apply ERROR args)
        ;; think about EVAL. Getting the environment right is subtle
          FIXP (safe-apply FIXP args)
          GENSYM (GENSYM)
          GREATERP (safe-apply GREATERP args)
          INTEROP (when (lax? INTEROP) (safe-apply INTEROP args))
          LESSP (safe-apply LESSP args)
          LIST (safe-apply LIST args)
          NUMBERP (safe-apply NUMBERP args)
          OBLIST (OBLIST)
          PLUS (safe-apply PLUS args)
          PRETTY (when (lax? 'PRETTY)
                   (safe-apply pretty-print args))
          PRINT (safe-apply print args)
          QUOTIENT (safe-apply QUOTIENT args)
          READ (READ)
          REMAINDER (safe-apply REMAINDER args)
          RPLACA (safe-apply RPLACA args)
          RPLACD (safe-apply RPLACD args)
          SET (safe-apply SET args)
          SYSIN (when (lax? 'SYSIN)
                  (safe-apply SYSIN args))
          SYSOUT (when (lax? 'SYSOUT)
                   (safe-apply SYSOUT args))
          TERPRI (println)
          TIMES (safe-apply TIMES args)
          TRACE (safe-apply TRACE args)
          UNTRACE (safe-apply UNTRACE args)
        ;; else
          (ex-info "No function found"
                   {:context "APPLY"
                    :function function-symbol
                    :args args})))))

(defn APPLY
  "Apply this `function` to these `arguments` in this `environment` and return
   the result.
   
   For bootstrapping, at least, a version of APPLY written in Clojure.
   All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
   See page 13 of the Lisp 1.5 Programmers Manual."
  [function args environment depth]
  (cond
    (= NIL function) (if (:strict *options*)
                       NIL
                       (throw (ex-info "NIL is not a function"
                                       {:context "APPLY"
                                        :function "NIL"
                                        :args args})))
    (= (ATOM? function) T) (apply-symbolic function args environment (inc depth))
    (= (first function) 'LAMBDA) (EVAL
                                  (CADDR function)
                                  (PAIRLIS (CADR function) args environment) depth)
    (= (first function) 'LABEL) (APPLY
                                 (CADDR function)
                                 args
                                 (make-cons-cell
                                  (make-cons-cell
                                   (CADR function)
                                   (CADDR function))
                                  environment)
                                 depth)))

(defn- EVCON
  "Inner guts of primitive COND. All `clauses` are assumed to be
  `beowulf.cons-cell/ConsCell` objects. Note that tests in Lisp 1.5
   often return `F`, not `NIL`, on failure.

   See page 13 of the Lisp 1.5 Programmers Manual."
  [clauses env depth]
  (let [test (EVAL (CAAR clauses) env depth)]
    (if
     (and (not= test NIL) (not= test 'F))
      (EVAL (CADAR clauses) env depth)
      (EVCON (CDR clauses) env depth))))

(defn- EVLIS
  "Map `EVAL` across this list of `args` in the context of this
  `env`ironment.All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 13 of the Lisp 1.5 Programmers Manual."
  [args env depth]
  (cond
    (= NIL args) NIL
    :else
    (make-cons-cell
     (EVAL (CAR args) env depth)
     (EVLIS (CDR args) env depth))))

(defn- eval-symbolic [^Symbol s env]
  (let [binding (ASSOC s env)]
    (if (= binding NIL)
      (throw (ex-info (format "No binding for symbol `%s`" s)
                      {:phase :eval
                       :symbol s}))
      (CDR binding))))


(defn EVAL
  "Evaluate this `expr` and return the result. If `environment` is not passed,
   it defaults to the current value of the global object list. The `depth`
   argument is part of the tracing system and should not be set by user code.

   All args are assumed to be numbers, symbols or `beowulf.cons-cell/ConsCell` 
   objects."
  ([expr]
   (EVAL expr @oblist 0))
  ([expr env depth]
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
           (= (CAR expr) 'COND) (EVCON (CDR expr) env depth)
           :else (APPLY
                  (CAR expr)
                  (EVLIS (CDR expr) env depth)
                  env
                  depth))
     :else (APPLY
            (CAR expr)
            (EVLIS (CDR expr) env depth)
            env
            depth))))

