(ns beowulf.host
  "provides Lisp 1.5 functions which can't be (or can't efficiently
   be) implemented in Lisp 1.5, which therefore need to be implemented in the
   host language, in this case Clojure.")

;; these are CANDIDATES to be host-implemented. only a subset of them MUST be.
;; those which can be implemented in Lisp should be, since that aids
;; portability.

;; RPLACA

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
