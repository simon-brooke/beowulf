(ns beowulf.functions
  (:require [beowulf.read :as r]
            [beowulf.print :as p])
  )

(defn car
  [sexpr]
  (if
    (list? sexpr)
    (first sexpr)
    (throw (Exception. "Undefined: car[" (p/prin sexpr) "]"))))

(defn cdr
  [sexpr]
  (if
    (list? sexpr)
    (rest sexpr)
    (throw (Exception. "Undefined: cdr[" (p/prin sexpr) "]"))))

(defn eq
  "eq, in LISP 1.5, is equality of atoms, only."
  [a b]
  (if
    (and (symbol? a)(symbol? b))
    (= a b)
    (throw (Exception. "Undefined: eq[" (p/prn a) ";" (p/prn b) "]"))))

(defn atom
  [sexpr]
  (symbol? sexpr))


