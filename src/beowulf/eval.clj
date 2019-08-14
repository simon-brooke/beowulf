(ns beowulf.eval
  (:require [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL]]))

(declare *oblist* primitive-eval)



(defn primitive-eval
  [x]
  (cond
    (number? x) x
    (symbol? x) (@*oblist* x)
    (instance? x beowulf.cons_cell.ConsCell)
    (apply (primitive-eval (.CAR x)) (map primitive-eval (.CDR x)))
    :else
    (throw (Exception. (str "Don't know how to eval `" x "`")))))


(def ^:dynamic *oblist*
  "The base environment."
  (atom {'NIL NIL
         'F NIL
         'T 'T
         'eval primitive-eval}))

