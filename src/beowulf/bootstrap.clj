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
  (:require [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell T]]
            [beowulf.host :refer [ASSOC ATOM CAAR CADAR CADDR CADR CAR CDR GET
                                  LIST NUMBERP PAIRLIS traced?]]
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

(defn try-resolve-subroutine
  "Attempt to resolve this `subr` with these `arg`."
  [subr args]
  (when (and subr (not= subr NIL))
    (try @(resolve subr)
         (catch Throwable any
           (throw (ex-info "Failed to resolve subroutine"
                           {:phase :apply
                            :function subr
                            :args args
                            :type :beowulf}
                           any))))))

(defn- trace-call
  "Show a trace of a call to the function named by this `function-symbol` 
  with these `args` at this depth."
  [function-symbol args depth]
  (when (traced? function-symbol)
    (let [indent (apply str (repeat depth "-"))]
      (println (str indent "> " function-symbol " " args)))))

(defn- trace-response
  "Show a trace of this `response` from the function named by this
   `function-symbol` at this depth."
  [function-symbol response depth]
  (when (traced? function-symbol)
    (let [indent (apply str (repeat depth "-"))]
      (println (str "<" indent " " function-symbol " " response))))
  response)

(defn- value
  "Seek a value for this symbol `s` by checking each of these indicators in
   turn."
  ([s]
   (value s (list 'APVAL 'EXPR 'FEXPR 'SUBR 'FSUBR)))
  ([s indicators]
   (when (symbol? s)
     (first (remove #(= % NIL) (map #(GET s %)
                                    indicators))))))

(defn- apply-symbolic
  "Apply this `funtion-symbol` to these `args` in this `environment` and 
   return the result."
  [^Symbol function-symbol args ^ConsCell environment depth]
  (trace-call function-symbol args depth)
  (let [lisp-fn ;; (try 
        (value function-symbol '(EXPR FEXPR))
                    ;; (catch Exception any (when (traced? function-symbol)
                    ;;                        (println any))))
        subr (value function-symbol '(SUBR FSUBR))
        host-fn (try-resolve-subroutine subr args)
        result (cond (and lisp-fn
                          (not= lisp-fn NIL)) (APPLY lisp-fn args environment depth)
                     host-fn (apply host-fn (when (instance? ConsCell args) args))
                     :else (ex-info "No function found"
                                    {:phase :apply
                                     :function function-symbol
                                     :args args
                                     :type :beowulf}))]
    (trace-response function-symbol result depth)
    result))

(defn APPLY
  "Apply this `function` to these `arguments` in this `environment` and return
   the result.
   
   For bootstrapping, at least, a version of APPLY written in Clojure.
   All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.
   See page 13 of the Lisp 1.5 Programmers Manual."
  [function args environment depth]
  (trace-call 'APPLY (list function args environment) depth)
  (let [result (cond
                 (= NIL function) (if (:strict *options*)
                                    NIL
                                    (throw (ex-info "NIL is not a function"
                                                    {:phase :apply
                                                     :function "NIL"
                                                     :args args
                                                     :type :beowulf})))
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
                                          :type :beowulf}))))]
    (trace-response 'APPLY result depth)
    result))

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

(defn- eval-symbolic
  [expr env depth]
  (let [v (value expr (list 'APVAL))
        indent (apply str (repeat depth "-"))]
    (when (traced? 'EVAL)
      (println (str indent ": EVAL: deep binding (" expr " . " (or v "nil") ")")))
    (if (and v (not= v NIL))
      v
      (let [v' (ASSOC expr env)]
        (when (traced? 'EVAL)
          (println (str indent ": EVAL: shallow binding: " (or v' "nil"))))
        (if (and v' (not= v' NIL))
          (.getCdr v')
          (throw (ex-info "No binding for symbol found"
                          {:phase :eval
                           :function 'EVAL
                           :args (list expr env depth)
                           :type :lisp
                           :code :A8})))))))

(defn EVAL
  "Evaluate this `expr` and return the result. If `environment` is not passed,
   it defaults to the current value of the global object list. The `depth`
   argument is part of the tracing system and should not be set by user code.

   All args are assumed to be numbers, symbols or `beowulf.cons-cell/ConsCell` 
   objects. However, if called with just a single arg, `expr`, I'll assume it's
   being called from the Clojure REPL and will coerce the `expr` to `ConsCell`."
  ([expr]
   (let [expr' (if (and (coll? expr) (not (instance? ConsCell expr)))
                 (make-beowulf-list expr)
                 expr)]
     (EVAL expr' @oblist 0)))
  ([expr env depth]
   (trace-call 'EVAL (list expr env depth) depth)
   (let [result (cond
                  (= NIL expr) NIL ;; it was probably a mistake to make Lisp 
                                   ;; NIL distinct from Clojure nil
                  (= (NUMBERP expr) T) expr
                  (symbol? expr) (eval-symbolic expr env depth)
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
                         depth))]
     (trace-response 'EVAL result depth)
     result)))

