(ns beowulf.host
  "provides Lisp 1.5 functions which can't be (or can't efficiently
   be) implemented in Lisp 1.5, which therefore need to be implemented in the
   host language, in this case Clojure."
  (:require [beowulf.cons-cell :refer [F make-beowulf-list NIL T]]
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

(defn PLUS2
  "Lisp 1.5 `PLUS` is varargs, and implementing varargs functions in Clojure is
   not an added complexity I want. So this is a two arg `PLUS`, on which a
  varargs `PLUS` can be built in the Lisp 1.5 layer using `REDUCE`."
  [x y]
  (let [s (+ x y)]
    (if (integer? s) s (float s))))

(defn TIMES2
  [x y]
  (let [p (* x y)]
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

(defn LIST
  [& args]
  (make-beowulf-list args))