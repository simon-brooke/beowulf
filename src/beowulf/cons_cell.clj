(ns beowulf.cons-cell
  )

(def NIL (symbol "NIL"))

(deftype ConsCell [CAR CDR]
  clojure.lang.ISeq
  (cons [this x] (ConsCell. x this))
  (count [this] (if
                 (= (.CDR this) NIL)
                  0
                  (inc (count (.CDR this)))))
  (first [this] (.CAR this))
  (more [this] (if
                 (= (.CDR this) NIL)
                 clojure.lang.PersistentList/EMPTY
                 (.CDR this)))
  (next [this] (.CDR this))
  (seq [this] this) ;; doesn't work - `Method beowulf/cons_cell/ConsCell.seq()Lclojure/lang/ISeq; is abstract`

  clojure.lang.IPersistentCollection
  (empty [this] false)
  (equiv [this other] false))

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
