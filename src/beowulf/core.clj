(ns beowulf.core
  "Essentially, the `-main` function and the bootstrap read-eval-print loop."
  (:require [beowulf.bootstrap :refer [EVAL]]
            [beowulf.io :refer [default-sysout SYSIN]]
            [beowulf.read :refer [READ read-from-console]]
            [beowulf.oblist :refer [*options* oblist]]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :refer [trim]]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; Copyright (C) 2022-2023 Simon Brooke
;;;
;;; This program is free software; you can redistribute it and/or
;;; modify it under the terms of the GNU General Public License
;;; as published by the Free Software Foundation; either version 2
;;; of the License, or (at your option) any later version.
;;; 
;;; This program is distributed in the hope that it will be useful,
;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;; GNU General Public License for more details.
;;; 
;;; You should have received a copy of the GNU General Public License
;;; along with this program; if not, write to the Free Software
;;; Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def stop-word "STOP")

(def cli-options
  [["-f FILEPATH" "--file-path FILEPATH"
    "Set the path to the directory for reading and writing Lisp files."
    :validate [#(and (.exists (io/file %))
                     (.isDirectory (io/file %))
                     (.canRead (io/file %))
                     (.canWrite (io/file %)))
               "File path must exist and must be a directory."]]
   ["-h" "--help"]
   ["-p PROMPT" "--prompt PROMPT" "Set the REPL prompt to PROMPT"
    :default "Sprecan::"]
   ["-r INITFILE" "--read INITFILE" "Read Lisp system from file INITFILE"
    :default default-sysout
    :validate [#(and
                 (.exists (io/file %))
                 (.canRead (io/file %)))
               "Could not find initfile"]]
   ["-s" "--strict" "Strictly interpret the Lisp 1.5 language, without extensions."]
   ["-t" "--time" "Time evaluations."]])

(defn repl
  "Read/eval/print loop."
  [prompt]
  (loop []
    (print prompt)
    (flush)
    (try
      (if-let [input (trim (read-from-console))]
        (if (= input stop-word)
          (throw (ex-info "\nFærwell!" {:cause :quit}))
          (println 
           (str ">  " 
                (print-str (if (:time *options*)
                             (time (EVAL (READ input) @oblist 0))
                             (EVAL (READ input) @oblist 0)))))) 
        (println))
      (catch
       Exception
       e
        (let [data (ex-data e)]
          (println (.getMessage e))
          (when
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
      ;; (pprint *options*)
      (when (:read *options*)
        (try (SYSIN (:read *options*))
             (catch Throwable any
               (println any))))
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
