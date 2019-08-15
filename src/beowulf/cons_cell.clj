(ns beowulf.cons-cell
  )

(def NIL (symbol "NIL"))

(def NIL (symbol "NIL"))

(deftype ConsCell [CAR CDR]
  clojure.lang.ISeq
  (cons [this x] (ConsCell. x this))
  (first [this] (.CAR this))
  ;; next and more must return ISeq:
  ;; https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/ISeq.java
  (more [this] (if
                 (= (.CDR this) NIL)
                 clojure.lang.PersistentList/EMPTY
                 (.CDR this)))
  (next [this] (if
                 (= (.CDR this) NIL)
                 nil ;; next returns nil when empty
                 (.CDR this)))

  clojure.lang.Seqable
  (seq [this] this)

  ;; for some reason this marker protocol is needed otherwise compiler complains
  ;; that `nth not supported on ConsCell`
  clojure.lang.Sequential

  clojure.lang.IPersistentCollection
  (count [this] (if
                 (= (.CDR this) NIL)
                  0
                  (inc (count (.CDR this)))))
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
