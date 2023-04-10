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
  (:require [beowulf.cons-cell :refer [F make-beowulf-list make-cons-cell
                                       pretty-print T]]
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

(declare APPLY EVAL prog-eval)

;;;; The PROGram feature ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def find-target
  (memoize
   (fn [target body]
     (loop [body' body]
       (cond
         (= body' NIL) (throw (ex-info (str "Mislar GO miercels: `" target "`")
                                       {:phase :lisp
                                        :function 'PROG
                                        :type :lisp
                                        :code :A6
                                        :target target}))
         (= (.getCar body') target) body'
         :else (recur (.getCdr body')))))))

(defn- prog-cond
  "Like `EVCON`, q.v. except using `prog-eval` instead of `EVAL` and not
   throwing an error if no clause matches."
  [clauses vars env depth]
  (loop [clauses' clauses]
    (if-not (= clauses' NIL)
      (let [test (prog-eval (CAAR clauses') vars env depth)]
        (if (not (#{NIL F} test))
          (prog-eval (CADAR clauses') vars env depth)
          (recur (.getCdr clauses'))))
      NIL)))

(defn- merge-vars [vars env]
  (reduce
   #(make-cons-cell
     (make-cons-cell %2 (@vars %2))
     env)
   env
   (keys @vars)))

(defn prog-eval
  "Like `EVAL`, q.v., except handling symbols, and expressions starting
   `GO`, `RETURN`, `SET` and `SETQ` specially."
  [expr vars env depth]
  (cond
    (number? expr) expr
    (symbol? expr) (@vars expr)
    (instance? ConsCell expr) (case (.getCar expr)
                                COND (prog-cond (.getCdr expr)
                                                vars env depth)
                                GO (make-cons-cell
                                    '*PROGGO* (.getCar (.getCdr expr)))
                                RETURN (make-cons-cell
                                        '*PROGRETURN*
                                        (prog-eval (.getCar (.getCdr expr))
                                                   vars env depth))
                                SET (let [v (CADDR expr)]
                                      (swap! vars
                                             assoc
                                             (prog-eval (CADR expr)
                                                        vars env depth)
                                             (prog-eval (CADDR expr)
                                                        vars env depth))
                                      v)
                                SETQ (let [v (CADDR expr)]
                                       (swap! vars
                                              assoc
                                              (CADR expr)
                                              (prog-eval v
                                                         vars env depth))
                                       v)
                                 ;; else
                                (beowulf.bootstrap/EVAL expr
                                                        (merge-vars vars env)
                                                        depth))))

(defn PROG
  "The accursed `PROG` feature. See page 71 of the manual.
   
   Lisp 1.5 introduced `PROG`, and most Lisps have been stuck with it ever 
   since. It introduces imperative programming into what should be a pure 
   functional language, and consequently it's going to be a pig to implement.
   
   Broadly, `PROG` is a variadic pseudo function called as a `FEXPR` (or 
   possibly an `FSUBR`, although I'm not presently sure that would even work.)

   The arguments, which are unevaluated, are a list of forms, the first of 
   which is expected to be a list of symbols which will be treated as names 
   of variables within the program, and the rest of which (the 'program body')
   are either lists or symbols. Lists are treated as Lisp expressions which
   may be evaulated in turn. Symbols are treated as targets for the `GO` 
   statement. 
      
   **GO:** 
   A `GO` statement takes the form of `(GO target)`, where 
   `target` should be one of the symbols which occur at top level among that
   particular invocation of `PROG`s arguments. A `GO` statement may occur at 
   top level in a PROG, or in a clause of a `COND` statement in a `PROG`, but
   not in a function called from the `PROG` statement. When a `GO` statement
   is evaluated, execution should transfer immediately to the expression which
   is the argument list immediately following the symbol which is its target.

   If the target is not found, an error with the code `A6` should be thrown.

   **RETURN:** 
   A `RETURN` statement takes the form `(RETURN value)`, where 
   `value` is any value. Following the evaluation of a `RETURN` statement, 
   the `PROG` should immediately exit without executing any further 
   expressions, returning the  value.

   **SET and SETQ:**
   In addition to the above, if a `SET` or `SETQ` expression is encountered
   in any expression within the `PROG` body, it should affect not the global
   object list but instead only the local variables of the program.

   **COND:**
   In **strict** mode, when in normal execution, a `COND` statement none of 
   whose clauses match should not return `NIL` but should throw an error with
   the code `A3`... *except* that inside a `PROG` body, it should not do so.
   *sigh*.

   **Flow of control:**
   Apart from the exceptions specified above, expressions in the program body
   are evaluated sequentially. If execution reaches the end of the program 
   body, `NIL` is returned.

   Got all that?

   Good."
  [program env depth]
  (let [trace (traced? 'PROG)
        vars (atom (reduce merge (map #(assoc {} % NIL) (.getCar program))))
        body (.getCdr program)
        targets (set (filter symbol? body))]
    (when trace (do
                  (println "Program:")
                  (pretty-print program))) ;; for debugging
    (loop [cursor body]
      (let [step (.getCar cursor)]
        (when trace (do (println "Executing step: " step)
                        (println "  with vars: " @vars)))
        (cond (= cursor NIL) NIL
              (symbol? step) (recur (.getCdr cursor))
              :else (let [v (prog-eval (.getCar cursor) vars env depth)]
                      (when trace (println "  --> " v))
                      (if (instance? ConsCell v)
                        (case (.getCar v)
                          *PROGGO* (let [target (.getCdr v)]
                                     (if (targets target)
                                       (recur (find-target target body))
                                       (throw (ex-info (str "Uncynlic GO miercels `"
                                                            target "`")
                                                       {:phase :lisp
                                                        :function 'PROG
                                                        :args program
                                                        :type :lisp
                                                        :code :A6
                                                        :target target
                                                        :targets targets}))))
                          *PROGRETURN* (.getCdr v)
                        ;; else
                          (recur (.getCdr cursor)))
                        (recur (.getCdr cursor)))))))))

;;;; Tracing execution ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

;;;; APPLY ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn try-resolve-subroutine
  "Attempt to resolve this `subr` with these `args`."
  [subr args]
  (when (and subr (not= subr NIL))
    (try @(resolve subr)
         (catch Throwable any
           (throw (ex-info "þegnung (SUBR) ne āfand"
                           {:phase :apply
                            :function subr
                            :args args
                            :type :beowulf}
                           any))))))

(defn- apply-symbolic
  "Apply this `funtion-symbol` to these `args` in this `environment` and 
   return the result."
  [^Symbol function-symbol args ^ConsCell environment depth]
  (trace-call function-symbol args depth)
  (let [lisp-fn (value function-symbol '(EXPR FEXPR))
        args' (cond (= NIL args) args
                    (empty? args) NIL
                    (instance? ConsCell args) args
                    :else (make-beowulf-list args))
        subr (value function-symbol '(SUBR FSUBR))
        host-fn (try-resolve-subroutine subr args')
        result (cond (and lisp-fn
                          (not= lisp-fn NIL)) (APPLY lisp-fn args' environment depth)
                     host-fn (try
                               (apply host-fn (when (instance? ConsCell args') args'))
                               (catch Exception any
                                 (throw (ex-info (str "Uncynlic þegnung: "
                                                      (.getMessage any))
                                                 {:phase :apply
                                                  :function function-symbol
                                                  :args args
                                                  :type :beowulf}
                                                 any))))
                     :else (ex-info "þegnung ne āfand"
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
                                    (throw (ex-info "NIL sí ne þegnung"
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
                         (throw (ex-info "Ungecnáwen wyrþan sí þegnung-weard"
                                         {:phase :apply
                                          :function function
                                          :args args
                                          :type :beowulf}))))]
    (trace-response 'APPLY result depth)
    result))

;;;; EVAL ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- EVCON
  "Inner guts of primitive COND. All `clauses` are assumed to be
  `beowulf.cons-cell/ConsCell` objects. Note that tests in Lisp 1.5
   often return `F`, not `NIL`, on failure. If no clause matches,
   then, strictly, we throw an error with code `:A3`.

   See pages 13 and 71 of the Lisp 1.5 Programmers Manual."
  [clauses env depth]
  (loop [clauses' clauses]
    (if-not (= clauses' NIL)
      (let [test (EVAL (CAAR clauses') env depth)]
        (if (not (#{NIL F} test))
         ;; (and (not= test NIL) (not= test F))
          (EVAL (CADAR clauses') env depth)
          (recur (.getCdr clauses'))))
      (if (:strict *options*)
        (throw (ex-info "Ne ġefōg dǣl in COND"
                        {:phase :eval
                         :function 'COND
                         :args (list clauses)
                         :type :lisp
                         :code :A3}))
        NIL))))

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
  (let [v (ASSOC expr env)
        indent (apply str (repeat depth "-"))]
    (when (traced? 'EVAL)
      (println (str indent ": EVAL: sceald bindele: " (or v "nil"))))
    (if (instance? ConsCell v)
      (.getCdr v)
      (let [v' (value expr (list 'APVAL))]
        (when (traced? 'EVAL)
          (println (str indent ": EVAL: deóp bindele: (" expr " . " (or v' "nil") ")")))
        (if v'
          v'
          (throw (ex-info "Ne tácen-bindele āfand"
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
     (EVAL expr' NIL 0)))
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
                                            COND (EVCON (CDR expr) env depth)
                                            FUNCTION (LIST 'FUNARG (CADR expr))
                                            PROG (PROG (CDR expr) env depth)
                                            QUOTE (CADR expr)
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

