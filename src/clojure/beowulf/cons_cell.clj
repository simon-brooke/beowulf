(ns beowulf.cons-cell
  "The fundamental cons cell on which all Lisp structures are built.
  Lisp 1.5 lists do not necessarily have a sequence as their CDR, so
  cannot be implemented on top of Clojure lists.")

;; (def NIL
;;   "The canonical empty list symbol."
;;   'NIL)

;; (def T
;;   "The canonical true value."
;;   'T) ;; true.

;; (def F
;;   "The canonical false value - different from `NIL`, which is not canonically
;;   false in Lisp 1.5."
;;   'F) ;; false as distinct from nil

(deftype ConsCell [^:unsynchronized-mutable car ^:unsynchronized-mutable cdr]
  ;; Note that, because the CAR and CDR fields are unsynchronised mutable - i.e.
  ;; plain old Java instance variables which can be written as well as read -
  ;; ConsCells are NOT thread safe. This does not matter, since Lisp 1.5 is
  ;; single threaded.

  (CAR [this] (.car this))
  (CDR [this] (.cdr this))
  (RPLACA
    [this value]
    (if
      (or
        (instance? beowulf.cons_cell.ConsCell value)
        (number? value)
        (symbol? value)
        (= value NIL))
    (do
      (set! (. cell CAR) value)
      cell)
    (throw (ex-info
             (str "Invalid value in RPLACA: `" value "` (" (type value) ")")
             {:cause :bad-value
              :detail :rplaca}))))

  clojure.lang.ISeq
  (cons [this x] (ConsCell. x this))
  (first [this] (.CAR this))
  ;; next and more must return ISeq:
  ;; https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/ISeq.java
  (more [this] (if
                 (seq? (.CDR this))
                 (.CDR this)
                 clojure.lang.PersistentList/EMPTY))
  (next [this] (if
                 (seq? (.CDR this))
                 (.CDR this)
                 nil ;; next returns nil when empty
                 ))

  clojure.lang.Seqable
  (seq [this] this)

  ;; for some reason this marker protocol is needed otherwise compiler complains
  ;; that `nth not supported on ConsCell`
  clojure.lang.Sequential

  clojure.lang.IPersistentCollection
  (count [this] (if
                  (coll? (.CDR this))
                  (inc (.count (.CDR this)))
                  1))
  (empty [this] false) ;; a cons cell is by definition not empty.
  (equiv [this other] (if
                        (seq? other)
                        (and
                          (if
                            (and
                              (seq? (first this))
                              (seq? (first other)))
                            (.equiv (first this) (first other))
                            (= (first this) (first other)))
                          (if
                            (and
                              (seq? (rest this))
                              (seq? (rest other)))
                            (.equiv (rest this) (rest other))
                            (= (rest this) (rest other))))
                        false)))

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

(defn pretty-print
  "This isn't the world's best pretty printer but it sort of works."
  ([^beowulf.cons_cell.ConsCell cell]
   (println (pretty-print cell 80 0)))
  ([^beowulf.cons_cell.ConsCell cell width level]
   (loop [c cell
          n (inc level)
          s "("]
     (if
       (instance? beowulf.cons_cell.ConsCell c)
       (let [car (.CAR c)
             cdr (.CDR c)
             cons? (instance? beowulf.cons_cell.ConsCell cdr)
             print-width (count (print-str c))
             indent (apply str (repeat n "  "))
             ss (str
                  s
                  (pretty-print car width n)
                  (cond
                    cons?
                    (if
                      (< (+ (count indent) print-width) width)
                      " "
                      (str "\n" indent))
                    (or (nil? cdr) (= cdr 'NIL))
                    ")"
                    :else
                    (str " . " (pretty-print cdr width n) ")")))]
         (if
           cons?
           (recur cdr n ss)
           ss))
       (str c)))))



(defmethod clojure.core/print-method
  ;;; I have not worked out how to document defmethod without blowing up the world.
  beowulf.cons_cell.ConsCell
  [this writer]
  (.write writer (to-string this)))


(defmacro make-cons-cell
  "Construct a new instance of cons cell with this `car` and `cdr`."
  [car cdr]
  `(ConsCell. ~car ~cdr))

(defn make-beowulf-list
  "Construct a linked list of cons cells with the same content as the
  sequence `x`."
  [x]
  (cond
    (empty? x) NIL
    (coll? x) (ConsCell.
                (if
                  (seq? (first x))
                  (make-beowulf-list (first x))
                  (first x))
                (make-beowulf-list (rest x)))
    :else
    NIL))
