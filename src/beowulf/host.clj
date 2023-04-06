(ns beowulf.host
  "provides Lisp 1.5 functions which can't be (or can't efficiently
   be) implemented in Lisp 1.5, which therefore need to be implemented in the
   host language, in this case Clojure."
  (:require [beowulf.cons-cell :refer [F make-beowulf-list make-cons-cell T]] ;; note hyphen - this is Clojure...
            [beowulf.gendoc :refer [open-doc]]
            [beowulf.oblist :refer [*options* NIL oblist]]
            [clojure.set :refer [union]]
            [clojure.string :refer [upper-case]])
  (:import [beowulf.cons_cell ConsCell] ;; note underscore - same namespace, but Java.
           ))

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

;; these are CANDIDATES to be host-implemented. only a subset of them MUST be.
;; those which can be implemented in Lisp should be, since that aids
;; portability.


(defn lax?
  "Are we in lax mode? If so. return true; is not, throw an exception with 
   this `symbol`."
  [symbol]
  (when (:strict *options*)
    (throw (ex-info (format "%s is not available in Lisp 1.5" symbol)
                    {:type :strict
                     :phase :host
                     :function symbol})))
  true)

;;;; Basic operations on cons cells ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn CONS
  "Construct a new instance of cons cell with this `car` and `cdr`."
  [car cdr]
  (beowulf.cons_cell.ConsCell. car cdr (gensym "c")))

(defn CAR
  "Return the item indicated by the first pointer of a pair. NIL is treated
  specially: the CAR of NIL is NIL."
  [x]
  (if
   (= x NIL) NIL
   (try
     (or (.getCar x) NIL)
     (catch Exception any
       (throw (ex-info
               (str "Cannot take CAR of `" x "` (" (.getName (.getClass x)) ")")
               {:phase :host
                :function 'CAR
                :args (list x)
                :type :beowulf}
               ;; startlingly, Lisp 1.5 did not flag an error when you took the
               ;; CAR of something that wasn't cons cell. The result, as the
               ;; manual says (page 56), could be garbage.
               any))))))

(defn CDR
  "Return the item indicated by the second pointer of a pair. NIL is treated
  specially: the CDR of NIL is NIL."
  [x]
  (if
   (= x NIL) NIL
   (try
     (.getCdr x)
     (catch Exception any
       (throw (ex-info
               (str "Cannot take CDR of `" x "` (" (.getName (.getClass x)) ")")
               {:phase :host
                :function 'CDR
                :args (list x)
                :type :beowulf}
               ;; startlingly, Lisp 1.5 did not flag an error when you took the
               ;; CAR of something that wasn't cons cell. The result, as the
               ;; manual says (page 56), could be garbage.
               any))))))

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
                 :expr   l}
                e))))))

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

(defn RPLACA
  "Replace the CAR pointer of this `cell` with this `value`. Dangerous, should
  really not exist, but does in Lisp 1.5 (and was important for some
  performance hacks in early Lisps)"
  [^ConsCell cell value]
  (if
   (instance? ConsCell cell)
    (if
     (or
      (instance? ConsCell value)
      (number? value)
      (symbol? value)
      (= value NIL))
      (try
        (.rplaca cell value)
        cell
        (catch Throwable any
          (throw (ex-info
                  (str (.getMessage any) " in RPLACA: `")
                  {:cause :upstream-error
                   :phase :host
                   :function :rplaca
                   :args (list cell value)
                   :type :beowulf}
                  any))))
      (throw (ex-info
              (str "Invalid value in RPLACA: `" value "` (" (type value) ")")
              {:cause :bad-value
               :phase :host
               :function :rplaca
               :args (list cell value)
               :type :beowulf})))
    (throw (ex-info
            (str "Invalid cell in RPLACA: `" cell "` (" (type cell) ")")
            {:cause :bad-cell
             :phase :host
             :function :rplaca
             :args (list cell value)
             :type :beowulf}))))

(defn RPLACD
  "Replace the CDR pointer of this `cell` with this `value`. Dangerous, should
  really not exist, but does in Lisp 1.5 (and was important for some
  performance hacks in early Lisps)"
  [^ConsCell cell value]
  (if
   (instance? ConsCell cell)
    (if
     (or
      (instance? ConsCell value)
      (number? value)
      (symbol? value)
      (= value NIL))
      (try
        (.rplacd cell value)
        cell
        (catch Throwable any
          (throw (ex-info
                  (str (.getMessage any) " in RPLACD: `")
                  {:cause :upstream-error
                   :phase :host
                   :function :rplacd
                   :args (list cell value)
                   :type :beowulf}
                  any))))
      (throw (ex-info
              (str "Invalid value in RPLACD: `" value "` (" (type value) ")")
              {:cause :bad-value
               :phase :host
               :function :rplacd
               :args (list cell value)
               :type :beowulf})))
    (throw (ex-info
            (str "Invalid cell in RPLACD: `" cell "` (" (type cell) ")")
            {:cause :bad-cell
             :phase :host
             :detail :rplacd
             :args (list cell value)
             :type :beowulf}))));; PLUS

(defn LIST
  [& args]
  (make-beowulf-list args))

;;;; Basic predicates ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro NULL
  "Returns `T` if and only if the argument `x` is bound to `NIL`; else `F`."
  [x]
  `(if (= ~x NIL) T F))

(defmacro NILP
  "Not part of LISP 1.5: `T` if `o` is `NIL`, else `NIL`."
  [x]
  `(if (= ~x NIL) T NIL))

(defn ATOM
  "Returns `T` if and only if the argument `x` is bound to an atom; else `F`.
  It is not clear to me from the documentation whether `(ATOM 7)` should return
  `T` or `F`. I'm going to assume `T`."
  [x]
  (if (or (symbol? x) (number? x)) T F))

(defmacro ATOM?
  "The convention of returning `F` from predicates, rather than `NIL`, is going
  to tie me in knots. This is a variant of `ATOM` which returns `NIL`
  on failure."
  [x]
  `(if (or (symbol? ~x) (number? ~x)) T NIL))

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

(defn AND
  "`T` if and only if none of my `args` evaluate to either `F` or `NIL`,
   else `F`.
   
   In `beowulf.host` principally because I don't yet feel confident to define
   varargs functions in Lisp."
  [& args]
  (cond (= NIL args) T
        (not (#{NIL F} (.getCar args))) (AND (.getCdr args))
        :else T))

(defn OR
  "`T` if and only if at least one of my `args` evaluates to something other
  than either `F` or `NIL`, else `F`.
   
   In `beowulf.host` principally because I don't yet feel confident to define
   varargs functions in Lisp."
  [& args]
  (cond (= NIL args) F
        (not (#{NIL F} (.getCar args))) T
        :else (OR (.getCdr args))))

;;;; Operations on lists ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; TODO: These are candidates for moving to Lisp urgently!

(defn ASSOC
  "If a is an association list such as the one formed by PAIRLIS in the above
  example, then assoc will produce the first pair whose first term is x. Thus
  it is a table searching function.

  All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 12 of the Lisp 1.5 Programmers Manual.
   
   **NOTE THAT** this function is overridden by an implementation in Lisp,
   but is currently still present for bootstrapping."
  [x a]
  (cond
    (= NIL a) NIL ;; this clause is not present in the original but is added for
    ;; robustness.
    (= (EQUAL (CAAR a) x) T) (CAR a)
    :else
    (ASSOC x (CDR a))))

(defn PAIRLIS
  "This function gives the list of pairs of corresponding elements of the
  lists `x` and `y`, and APPENDs this to the list `a`. The resultant list
  of pairs, which is like a table with two columns, is called an
  association list.

  Eessentially, it builds the environment on the stack, implementing shallow
  binding.

  All args are assumed to be `beowulf.cons-cell/ConsCell` objects.
  See page 12 of the Lisp 1.5 Programmers Manual.
   
   **NOTE THAT** this function is overridden by an implementation in Lisp,
   but is currently still present for bootstrapping."
  [x y a]
  (cond
    ;; the original tests only x; testing y as well will be a little more
    ;; robust if `x` and `y` are not the same length.
    (or (= NIL x) (= NIL y)) a
    :else (make-cons-cell
           (make-cons-cell (CAR x) (CAR y))
           (PAIRLIS (CDR x) (CDR y) a))))

;;;; Arithmetic ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; TODO: When in strict mode, should we limit arithmetic precision to that
;; supported by Lisp 1.5?

(defn PLUS
  [& args]
  (let [s (apply + args)]
    (if (integer? s) s (float s))))

(defn TIMES
  [& args]
  (let [p (apply * args)]
    (if (integer? p) p (float p))))

(defn DIFFERENCE
  [x y]
  (let [d (- x y)]
    (if (integer? d) d (float d))))

(defn QUOTIENT
  "I'm not certain from the documentation whether Lisp 1.5 `QUOTIENT` returned
  the integer part of the quotient, or a realnum representing the whole
  quotient. I am for now implementing the latter."
  [x y]
  (let [q (/ x y)]
    (if (integer? q) q (float q))))

(defn REMAINDER
  [x y]
  (rem x y))

(defn ADD1
  [x]
  (inc x))

(defn SUB1
  [x]
  (dec x))

(defn FIXP
  [x]
  (if (integer? x) T F))

(defn NUMBERP
  [x]
  (if (number? x) T F))

(defn LESSP
  [x y]
  (if (< x y) T F))

(defn GREATERP
  [x y]
  (if (> x y) T F))

;;;; Miscellaneous ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn GENSYM
  "Generate a unique symbol."
  []
  (symbol (upper-case (str (gensym "SYM")))))

(defn ERROR
  "Throw an error"
  [& args]
  (throw (ex-info "LISP ERROR" {:args args
                                :phase :eval
                                :function 'ERROR
                                :type :lisp
                                :code (or (first args) 'A1)})))

;;;; Assignment and the object list ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn OBLIST
  "Return a list of the symbols currently bound on the object list.
   
   **NOTE THAT** in the Lisp 1.5 manual, footnote at the bottom of page 69, it implies 
   that an argument can be passed but I'm not sure of the semantics of
   this."
  []
  (if (instance? ConsCell @oblist)
    (make-beowulf-list (map CAR @oblist))
    NIL))

(def magic-marker
  "The unexplained magic number which marks the start of a property list."
  (Integer/parseInt "77777" 8))

(defn PUT
  "Put this `value` as the value of the property indicated by this `indicator` 
   of this `symbol`. Return `value` on success.
   
   NOTE THAT there is no `PUT` defined in the manual, but it would have been 
   easy to have defined it so I don't think this fully counts as an extension."
  [symbol indicator value]
  (if-let [binding (ASSOC symbol @oblist)]
    (if-let [prop (ASSOC indicator (CDDR binding))]
      (RPLACD prop value)
      (RPLACD binding
              (make-cons-cell
               magic-marker
               (make-cons-cell
                indicator
                (make-cons-cell value (CDDR binding))))))
    (swap!
     oblist
     (fn [ob s p v]
       (make-cons-cell
        (make-beowulf-list (list s magic-marker p v))
        ob))
     symbol indicator value)))

(defn GET
  "From the manual:
   
   '`get` is somewhat like `prop`; however its value is car of the rest of
   the list if the `indicator` is found, and NIL otherwise.'
   
   It's clear that `GET` is expected to be defined in terms of `PROP`, but
   we can't implement `PROP` here because we lack `EVAL`; and we can't have
   `EVAL` here because both it and `APPLY` depends on `GET`.
   
   OK, It's worse than that: the statement of the definition of `GET` (and 
   of) `PROP` on page 59 says that the first argument to each must be a list;
   But the in the definition of `ASSOC` on page 70, when `GET` is called its
   first argument is always an atom. Since it's `ASSOC` and `EVAL` which I 
   need to make work, I'm going to assume that page 59 is wrong."
  [symbol indicator]
  (let [binding (ASSOC symbol @oblist)]
    (cond
      (= binding NIL) NIL
      (= magic-marker (CADR binding)) (loop [b binding]
                                        (cond (= b NIL) NIL
                                              (= (CAR b) indicator) (CADR b)
                                              :else (recur (CDR b))))
      :else (throw
             (ex-info "Misformatted property list (missing magic marker)"
                      {:phase :host
                       :function :get
                       :args (list symbol indicator)
                       :type :beowulf})))))

(defn DEFLIST
  "For each pair in this association list `a-list`, set the property with this
   `indicator` of the symbol which is the first element of the pair to the 
   value which is the second element of the pair. See page 58 of the manual."
  [a-list indicator]
  (map
   #(PUT (CAR %) indicator (CDR %))
   a-list))

(defn DEFINE
  "Bootstrap-only version of `DEFINE` which, post boostrap, can be overwritten 
  in LISP. 

  The single argument to `DEFINE` should be an association list of symbols to
   lambda functions. See page 58 of the manual."
  [a-list]
  (DEFLIST a-list 'EXPR))

(defn SET
  "Implementation of SET in Clojure. Add to the `oblist` a binding of the
   value of `var` to the value of `val`. NOTE WELL: this is not SETQ!"
  [symbol val]
  (PUT symbol 'APVAL val))

;;;; TRACE and friends ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def traced-symbols
  "Symbols currently being traced."
  (atom #{}))

(defn traced?
  "Return `true` iff `s` is a symbol currently being traced, else `nil`."
  [s]
  (try (contains? @traced-symbols s)
       (catch Throwable _ nil)))

(defn TRACE
  "Add this `s` to the set of symbols currently being traced. If `s`
   is not a symbol or sequence of symbols, does nothing."
  [s]
  (swap! traced-symbols
         #(cond
            (symbol? s) (conj % s)
            (and (seq? s) (every? symbol? s)) (union % (set s))
            :else %)))

(defn UNTRACE
  "Remove this `s` from the set of symbols currently being traced. If `s`
   is not a symbol or sequence of symbols, does nothing."
  [s]
  (cond
    (symbol? s) (swap! traced-symbols #(set (remove (fn [x] (= s x)) %)))
    (and (seq? s) (every? symbol? s)) (map UNTRACE s))
  @traced-symbols)

;;;; Extensions ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn DOC
  "Open the page for this `symbol` in the Lisp 1.5 manual, if known, in the 
    default web browser.
   
   **NOTE THAT** this is an extension function, not available in strct mode."
  [symbol]
  (when (lax? 'DOC)
    (open-doc symbol)))

(defn CONSP
  "Return `T` if object `o` is a cons cell, else `F`.
   
   **NOTE THAT** this is an extension function, not available in strct mode. 
   I believe that Lisp 1.5 did not have any mechanism for testing whether an
   argument was, or was not, a cons cell."
  [o]
  (when (lax? 'CONSP)
    (if (instance? o ConsCell) 'T 'F)))