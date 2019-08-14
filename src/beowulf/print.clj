(ns beowulf.print
;;  (:require [beowulf.cons-cell])
  )

(defprotocol Printable
  (prin [x]))

(extend-type
  clojure.lang.Symbol
  Printable
  (prin [x] (str x)))

(extend-protocol
  Printable
  nil
  (prin [x] "NIL"))

(extend-protocol
  Printable

  clojure.lang.Symbol
  (prin [x] (str x))

  java.lang.Number
  (prin [x] (str x))

  beowulf.cons_cell.ConsCell
  (prin [x]
        (let [car (.CAR x)
              cdr (.CDR x)]
        (str
          "("
          (prin (.CAR x))
          " . "
          (prin (.CDR x))
          ")")))

  java.lang.Object
  (prin
    [x]
    (str x)))

