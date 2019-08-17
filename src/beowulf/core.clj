(ns beowulf.core
  (:require [beowulf.eval :refer [EVAL oblist]]
            [beowulf.read :refer [READ]])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (loop []
    (print ":: ")
    (flush)
    (let [input (READ)]
      (println (str "\tI read: " input))
      (println (str ">  " (EVAL input @oblist)))
      (recur))))
