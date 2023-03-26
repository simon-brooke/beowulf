(ns beowulf.cons-cell
  "The fundamental cons cell on which all Lisp structures are built.
  Lisp 1.5 lists do not necessarily have a sequence as their CDR, and
  must have both CAR and CDR mutable, so cannot be implemented on top
  of Clojure lists."
  (:require [beowulf.oblist :refer [NIL]]))

(declare cons-cell?)

(def T
  "The canonical true value."
  (symbol "T")) ;; true.

(def F
  "The canonical false value - different from `NIL`, which is not canonically
  false in Lisp 1.5."
  (symbol "F")) ;; false as distinct from nil

(defprotocol MutableSequence
  "Like a sequence, but mutable."
  (rplaca
    [this value]
    "replace the first element of this sequence with this value")
  (rplacd
    [this value]
    "replace the rest (but-first; cdr) of this sequence with this value")
  (getCar
    [this]
    "Return the first element of this sequence.")
  (getCdr
    [this]
    "like `more`, q.v., but returns List `NIL` not Clojure `nil` when empty.")
  (getUid
   [this]
   "Returns a unique identifier for this object")
  )

(deftype ConsCell [^:unsynchronized-mutable CAR ^:unsynchronized-mutable CDR uid]
  ;; Note that, because the CAR and CDR fields are unsynchronised mutable - i.e.
  ;; plain old Java instance variables which can be written as well as read -
  ;; ConsCells are NOT thread safe. This does not matter, since Lisp 1.5 is
  ;; single threaded.
  MutableSequence

  (rplaca [this value]
    (if
     (or
      (satisfies? MutableSequence value) ;; can't reference
              ;; beowulf.cons_cell.ConsCell,
              ;; because it is not yet
              ;; defined
      (cons-cell? value)
      (number? value)
      (symbol? value))
      (do
        (set! (. this CAR) value)
        this)
      (throw (ex-info
              (str "Invalid value in RPLACA: `" value "` (" (type value) ")")
              {:cause  :bad-value
               :detail :rplaca}))))

  (rplacd [this value]
    (if
     (or
      (satisfies? MutableSequence value)
      (cons-cell? value)
      (number? value)
      (symbol? value))
      (do
        (set! (. this CDR) value)
        this)
      (throw (ex-info
              (str "Invalid value in RPLACD: `" value "` (" (type value) ")")
              {:cause  :bad-value
               :detail :rplaca}))))
  
  (getCar [this]
    (. this CAR))
  (getCdr [this]
    (. this CDR)) 
  (getUid [this]
    (. this uid))

  clojure.lang.ISeq
  (cons [this x] (ConsCell. x this (gensym "c")))
  (first [this] (.CAR this))
  ;; next and more must return ISeq:
  ;; https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/ISeq.java
  (more [this] (if
                (seq? (.getCdr this))
                 (.getCdr this)
                 clojure.lang.PersistentList/EMPTY))
  (next [this] (if
                (seq? (.getCdr this))
                 (.getCdr this)
                 nil ;; next returns nil when empty
                 ))

  clojure.lang.Seqable
  (seq [this] this)

  ;; for some reason this marker protocol is needed otherwise compiler complains
  ;; that `nth not supported on ConsCell`
  clojure.lang.Sequential

  clojure.lang.IPersistentCollection
  (empty [this] (= this NIL)) ;; a cons cell is by definition not empty.
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
                           (seq? (.getCdr this))
                           (seq? (.getCdr other)))
                           (.equiv (.getCdr this) (.getCdr other))
                           (= (.getCdr this) (.getCdr other))))
                        false))

  clojure.lang.Counted
  (count [this] (loop [cell this
                       result 1]
                  (if
                   (and (coll? (.getCdr cell)) (not= NIL (.getCdr cell)))
                    (recur (.getCdr cell) (inc result))
                    result)))

  java.lang.Object
  (toString [this]
    (str "("
         (. this CAR)
         (cond
           (instance? ConsCell (. this CDR)) (str " " (subs (.toString (. this CDR)) 1))
           (= NIL (. this CDR)) ")"
           :else (str " . " (. this CDR))))))

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
      (let [car (.first c)
            cdr (.getCdr c)
            cons? (and
                   (instance? beowulf.cons_cell.ConsCell cdr)
                   (not (nil? cdr))
                   (not= cdr NIL))
            ss (str
                s
                (to-string car)
                (cond
                  (or (nil? cdr) (= cdr NIL))
                  ")"
                  cons?
                  " "
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
       (let [car (.first c)
             cdr (.getCdr c)
             cons? (instance? beowulf.cons_cell.ConsCell cdr)
             print-width (count (print-str c))
             indent (apply str (repeat n "  "))
             ss (str
                 s
                 (pretty-print car width n)
                 (cond
                   (or (nil? cdr) (= cdr NIL))
                   ")"
                   cons?
                   (if
                    (< (+ (count indent) print-width) width)
                     " "
                     (str "\n" indent))
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

(defn make-cons-cell
  "Construct a new instance of cons cell with this `car` and `cdr`."
  [car cdr]
  (try
    (ConsCell. car cdr (gensym "c"))
    (catch Exception any
      (throw (ex-info "Cound not construct cons cell" {:car car
                                                       :cdr cdr} any)))))

(defn cons-cell?
  "Is this object `o` a beowulf cons-cell?"
  [o]
  (instance? beowulf.cons_cell.ConsCell o))

(defn make-beowulf-list
  "Construct a linked list of cons cells with the same content as the
  sequence `x`."
  [x]
  (try
    (cond
      (empty? x) NIL
      (coll? x) (ConsCell.
                 (if
                  (coll? (first x))
                   (make-beowulf-list (first x))
                   (first x))
                 (make-beowulf-list (rest x))
                 (gensym "c"))
      :else
      NIL)
    (catch Exception any
      (throw (ex-info "Could not construct Beowulf list"
                      {:content x}
                      any)))))

(defn CONS
  "Construct a new instance of cons cell with this `car` and `cdr`."
  [car cdr]
  (beowulf.cons_cell.ConsCell. car cdr (gensym "c")))

(defn CAR
  "Return the item indicated by the first pointer of a pair. NIL is treated
  specially: the CAR of NIL is NIL."
  [x]
  (if
   (= x NIL) NIL
   (try
     (or (.getCar x) NIL)
     (catch Exception any
       (throw (Exception.
               (str "Cannot take CAR of `" x "` (" (.getName (.getClass x)) ")") any))))))

(defn CDR
  "Return the item indicated by the second pointer of a pair. NIL is treated
  specially: the CDR of NIL is NIL."
  [x]
  (if
   (= x NIL) NIL
   (try
     (.getCdr x)
     (catch Exception any
       (throw (Exception.
               (str "Cannot take CDR of `" x "` (" (.getName (.getClass x)) ")") any))))))

(defn LIST
  [& args]
  (make-beowulf-list args))