(ns beowulf.oblist
  "A namespace mainly devoted to the object list.
   
   Yes, this makes little sense, but if you put it anywhere else you end
   up in cyclic dependency hell."
  )

(def NIL
  "The canonical empty list symbol."
  'NIL)

(def oblist
  "The default environment."
  (atom NIL))

(def ^:dynamic *options*
  "Command line options from invocation."
  {})

