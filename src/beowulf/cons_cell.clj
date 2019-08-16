(ns beowulf.cons-cell
  )

(def NIL (symbol "NIL"))

(def T (symbol "T")) ;; true.

(def F (symbol "F")) ;; false as distinct from nil

(deftype ConsCell [CAR CDR]
  clojure.lang.ISeq
  (cons [this x] (ConsCell. x this))
  (first [this] (.CAR this))
  ;; next and more must return ISeq:
  ;; https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/ISeq.java
  (more [this] (if
                 (seq? this)
                 (if
                   (= (.CDR this) NIL)
                   clojure.lang.PersistentList/EMPTY
                   (.CDR this))
                 NIL))
  (next [this] (if
                 (seq? this)
                 (if
                   (= (.CDR this) NIL)
                   nil ;; next returns nil when empty
                   (.CDR this))
                 NIL))

  clojure.lang.Seqable
  (seq [this] this)

  ;; for some reason this marker protocol is needed otherwise compiler complains
  ;; that `nth not supported on ConsCell`
  clojure.lang.Sequential

  clojure.lang.IPersistentCollection
  (count [this] (if
                  (seq? this)
                  (if
                    (= (.CDR this) NIL)
                    0
                    (inc (count (.CDR this))))
                  0))
  (empty [this] false)
  (equiv [this other] false))

(defn- to-string
  "Printing ConsCells gave me a *lot* of trouble. This is an internal function
  used by the print-method override (below) in order that the standard Clojure
  `print` and `str` functions will print ConsCells correctly. The argument
  `cell` must, obviously, be an instance of `ConsCell`."
  [cell]
  (loop [c cell
         n 0
         s "("]
    (if
      (instance? beowulf.cons_cell.ConsCell c)
      (let [car (.CAR c)
            cdr (.CDR c)
            cons? (instance? beowulf.cons_cell.ConsCell cdr)
            ss (str
                 s
                 (to-string car)
                 (cond
                   cons?
                   " "
                   (or (nil? cdr) (= cdr 'NIL))
                   ")"
                   :else
                   (str " . " (to-string cdr) ")")))]
        (if
          cons?
          (recur cdr (inc n) ss)
          ss))
      (str c))))

(defmethod clojure.core/print-method beowulf.cons_cell.ConsCell
  [this writer]
  (.write writer (to-string this)))


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
