(ns beowulf.cons-cell
  )

(def NIL (symbol "NIL"))

(deftype ConsCell [CAR CDR])

(defn make-cons-cell
  [a d]
  (ConsCell. a d))

(defn make-beowulf-list
  [x]
  (cond
    (empty? x) NIL
    (coll? x) (ConsCell.
                (first x)
                (make-beowulf-list (rest x)))
    :else
    NIL))
