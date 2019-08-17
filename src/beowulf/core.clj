(ns beowulf.core
  (:require [beowulf.eval :refer [EVAL oblist]]
            [beowulf.read :refer [READ]])
  (:gen-class))

(defn -main
  "Read/eval/print loop."
  [& args]
  (println "Béowulf is mín nama")
  (loop []
    (print ":: ")
    (flush)
    (let [input (READ)]
      (println (str ">  " (EVAL input @oblist)))
      (recur))))
