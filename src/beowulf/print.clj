(ns beowulf.print
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
        (loop [c x
               n 0
               s "("]
          (let [car (.CAR c)
                cdr (.CDR c)
                cons? (instance? beowulf.cons_cell.ConsCell cdr)
                ss (str
                     s
                     (prin car)
                     (cond
                       cons?
                       " "
                       (or (nil? cdr) (= cdr 'NIL))
                       ")"
                       :else
                       (str " . " (prin cdr) ")")))]
            (if
              cons?
              (recur cdr (inc n) ss)
              ss))))

  java.lang.Object
  (prin
    [x]
    (str x)))

