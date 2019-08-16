(ns beowulf.core
  (:require [beowulf.eval :refer [primitive-eval oblist]]
            [beowulf.read :refer [primitive-read]])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (loop []
    (print ":: ")
    (flush)
    (let [input (primitive-read)]
      (println (str "\tI read: " input))
      (println (str ">  " (primitive-eval input @oblist)))
      (recur))))
