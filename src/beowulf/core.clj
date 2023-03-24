(ns beowulf.core
  "Essentially, the `-main` function and the bootstrap read-eval-print loop."
  (:require [beowulf.bootstrap :refer [EVAL oblist *options*]]
            [beowulf.read :refer [READ]]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :refer [trim]]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def stop-word "STOP")

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
      ;; TODO: does not currently allow the reading of forms covering multiple
      ;; lines.
      (let [input (trim (read-line))]
        (cond
          (= input stop-word) (throw (ex-info "\nFærwell!" {:cause :quit}))
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
              :strict nil ;; the message, which has already been printed, is enough.
              :quit (throw e)
              ;; default
              (pprint data))))))
    (recur)))

(defn -main
  "Parse options, print the banner, read the init file if any, and enter the
  read/eval/print loop."
  [& opts]
  (let [args (parse-opts opts cli-options)]
    (println
      (str
        "\nHider wilcuman. Béowulf is mín nama.\n"
        (when
          (System/getProperty "beowulf.version")
          (str "Síðe " (System/getProperty "beowulf.version") "\n"))
        (when
          (:help (:options args))
          (:summary args))
        (when (:errors args)
          (apply str (interpose "; " (:errors args))))
        "\nSprecan '" stop-word "' tó laéfan\n"))
    (binding [*options* (:options args)]
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
