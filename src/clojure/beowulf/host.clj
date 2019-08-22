(ns beowulf.host
  "provides Lisp 1.5 functions which can't be (or can't efficiently
   be) implemented in Lisp 1.5, which therefore need to be implemented in the
   host language, in this case Clojure."
  (:require [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL T F]]))

;; these are CANDIDATES to be host-implemented. only a subset of them MUST be.
;; those which can be implemented in Lisp should be, since that aids
;; portability.

;; RPLACA

(defn RPLACA
  [^beowulf.cons_cell.ConsCell cell value]
  (if
    (instance? beowulf.cons_cell.ConsCell cell)
    (if
      (or
        (instance? beowulf.cons_cell.ConsCell value)
        (number? value)
        (symbol? value)
        (= value NIL))
    (do
      (set! (. cell CAR) value)
      cell)
    (throw (ex-info
             (str "Invalid value in RPLACA: `" value "` (" (type value) ")")
             {:cause :bad-value
              :detail :rplaca})))
    (throw (ex-info
             (str "Invalid cell in RPLACA: `" cell "` (" (type cell) ")")
             {:cause :bad-value
              :detail :rplaca}))))

;; RPLACD

;; PLUS

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
