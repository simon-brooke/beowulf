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
  (:require [beowulf.cons-cell :refer [make-cons-cell T]]
            [beowulf.host :refer [ATOM CAAR CADAR CADDR CADR CAR CDR GET LIST
                                  NUMBERP PAIRLIS traced?]]
            [beowulf.interop :refer [to-clojure]]
            [beowulf.oblist :refer [*options* NIL oblist]])
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
        (if function-symbol
          (let [f (GET function-symbol 'SUBR)]
            (when f
              (apply @(resolve f) (to-clojure args))))
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
    (= (ATOM function) T) (apply-symbolic function args environment (inc depth))
    :else (case (first function)
            LABEL (APPLY
                   (CADDR function)
                   args
                   (make-cons-cell
                    (make-cons-cell
                     (CADR function)
                     (CADDR function))
                    environment)
                   depth)
            FUNARG (APPLY (CADR function) args (CADDR function) depth)
            LAMBDA (EVAL
                    (CADDR function)
                    (PAIRLIS (CADR function) args environment) depth)
            (throw (ex-info "Unrecognised value in function position"
                            {:phase :apply
                             :function function
                             :args args
                             :type :beowulf})))))

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

;; (defn- eval-symbolic [^Symbol s env]
;;   (let [binding (ASSOC s env)]
;;     (if (= binding NIL)
;;       (throw (ex-info (format "No binding for symbol `%s`" s)
;;                       {:phase :eval
;;                        :symbol s}))
;;       (CDR binding))))

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
     (symbol? expr) (GET expr 'APVAL)
     (string? expr) (if (:strict *options*)
                      (throw
                       (ex-info
                        (str "EVAL: strings not allowed in strict mode: \"" expr "\"")
                        {:phase  :eval
                         :detail :strict
                         :expr   expr}))
                      (symbol expr))
     (= (ATOM (CAR expr)) T) (case (CAR expr)
                               QUOTE (CADR expr)
                               FUNCTION (LIST 'FUNARG (CADR expr))
                               COND (EVCON (CDR expr) env depth)
           ;; else 
                               (APPLY
                                (CAR expr)
                                (EVLIS (CDR expr) env depth)
                                env
                                depth))
     :else (APPLY
            (CAR expr)
            (EVLIS (CDR expr) env depth)
            env
            depth))))

