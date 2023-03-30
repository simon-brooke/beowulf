(ns beowulf.oblist
  "A namespace mainly devoted to the object list.
   
   Yes, this makes little sense, but if you put it anywhere else you end
   up in cyclic dependency hell."
  )

(def NIL
  "The canonical empty list symbol.
   
   TODO: this doesn't really work, because (from Clojure) `(empty? NIL)` throws
   an exception. It might be better to subclass beowulf.cons_cell.ConsCell to create
   a new singleton class Nil which overrides the `empty` method of 
   IPersistentCollection?"
  'NIL)

(def oblist
  "The default environment."
  (atom NIL))

(def ^:dynamic *options*
  "Command line options from invocation."
  {})

