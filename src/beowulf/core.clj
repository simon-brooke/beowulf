(ns beowulf.core
  (:require [beowulf.eval :refer [primitive-eval oblist]]
            [beowulf.read :refer [primitive-read]]
            [beowulf.print :refer [primitive-print prin]])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (loop []
    (print ":: ")
    (flush)
    (let [input (primitive-read)]
      (println (str "\tI read: " (prin input)))
      (println (str ">  "(prin (primitive-eval input @oblist))))
      (recur))))
