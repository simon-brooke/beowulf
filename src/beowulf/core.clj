(ns beowulf.core
  (:require [beowulf.bootstrap :refer [EVAL oblist *trace?*]]
            [beowulf.read :refer [READ]]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.cli :refer [parse-opts]]
            [environ.core :refer [env]])
  (:gen-class))

(def cli-options
  [["-h" "--help"]
   ["-p PROMPT" "--prompt PROMPT" "Set the REPL prompt to PROMPT"
    :default "Sprecan::"]
   ["-r INITFILE" "--read INITFILE" "Read Lisp functions from the file INITFILE"
    :validate [#(and
                  (.exists (io/file %))
                  (.canRead (io/file %)))
               "Could not find initfile"]]
   ["-s" "--strict" "Strictly interpret the Lisp 1.5 language, without extensions."]
   ["-t" "--trace" "Trace Lisp evaluation."]])

(defn repl
  "Read/eval/print loop."
  [prompt]
  (loop []
    (print prompt)
    (flush)
    (try
      (let [input (read-line)]
        (cond
          (= input "quit") (throw (ex-info "\nFærwell!" {:cause :quit}))
          input (println (str ">  " (print-str (EVAL (READ input) @oblist))))
          :else (println)))
      (catch
        Exception
        e
        (let [data (ex-data e)]
          (println (.getMessage e))
          (if
            data
            (case (:cause data)
              :parse-failure (println (:failure data))
              :quit (throw e)
              ;; default
              (pprint data))))))
    (recur)))

(defn -main
  [& opts]
  (let [args (parse-opts opts cli-options)]
    (println
      (str
        "\nHider wilcuman. Béowulf is mín nama.\n"
        (if
          (System/getProperty "beowulf.version")
          (str "Síðe " (System/getProperty "beowulf.version") "\n"))
        (if
          (:help (:options args))
          (:summary args))
        (if (:errors args)
          (apply str (interpose "; " (:errors args))))
        "\nSprecan 'quit' tó laéfan\n"))
    (binding [*trace?* (true? (:trace (:options args)))]
      (try
        (repl (str (:prompt (:options args)) " "))
        (catch
          Exception
          e
          (let [data (ex-data e)]
            (if
              data
              (case (:cause data)
                :quit nil
                ;; default
                (pprint data))
              (println e))))))))
