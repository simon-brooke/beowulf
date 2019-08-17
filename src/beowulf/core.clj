(ns beowulf.core
  (:require [beowulf.eval :refer [EVAL oblist]]
            [beowulf.read :refer [READ]]
            [clojure.pprint :refer [pprint]]
            [environ.core :refer [env]])
  (:gen-class))

(defn repl
  "Read/eval/print loop."
  []
  (loop []
    (print ":: ")
    (flush)
    (try
      (let [input (read-line)]
        (cond
          (= input "quit") (throw (ex-info "Færwell!" {:cause :quit}))
          input (println (str ">  " (EVAL (READ input) @oblist)))
          :else (println)))
      (catch
        Exception
        e
        (let [data (ex-data e)]
          (if
            data
            (case (:cause data)
              :parse-failure (println (:failure data))
              :quit (throw e)
              ;; default
              (pprint data))
            (println (.getMessage e))))))
    (recur)))

(defn -main
  [& args]
  (println
    (str
      "Hider wilcuman. Béowulf is mín nama\nSíðe "
      (System/getProperty "beowulf.version")
      "\n\n"))
  (try
    (repl)
    (catch
      Exception
      e
      (let [data (ex-data e)]
        (if
          data
          (case (:cause data)
            :quit (println (.getMessage e))
            ;; default
            (pprint data))
          (println e))))))
