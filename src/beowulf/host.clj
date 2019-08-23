(ns beowulf.host
  "provides Lisp 1.5 functions which can't be (or can't efficiently
   be) implemented in Lisp 1.5, which therefore need to be implemented in the
   host language, in this case Clojure."
  (:require [beowulf.cons-cell :refer [T NIL F]]
            ;; note hyphen - this is Clojure...
            )
  (:import [beowulf.cons_cell ConsCell]
           ;; note underscore - same namespace, but Java.
           ))

;; these are CANDIDATES to be host-implemented. only a subset of them MUST be.
;; those which can be implemented in Lisp should be, since that aids
;; portability.


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
    (do
      (.rplaca cell value)
      cell)
    (throw (ex-info
             (str "Invalid value in RPLACA: `" value "` (" (type value) ")")
             {:cause :bad-value
              :detail :rplaca})))
    (throw (ex-info
             (str "Invalid cell in RPLACA: `" cell "` (" (type cell) ")")
             {:cause :bad-value
              :detail :rplaca}))))

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
    (do
      (.rplacd cell value)
      cell)
    (throw (ex-info
             (str "Invalid value in RPLACD: `" value "` (" (type value) ")")
             {:cause :bad-value
              :detail :rplaca})))
    (throw (ex-info
             (str "Invalid cell in RPLACD: `" cell "` (" (type cell) ")")
             {:cause :bad-value
              :detail :rplaca}))));; PLUS

;; MINUS

;; DIFFERENCE

;; QUOTIENT

;; REMAINDER

;; ADD1

;; SUB1

;; MAX

;; MIN

;; RECIP

;; FIXP

;; NUMBERP

;;
