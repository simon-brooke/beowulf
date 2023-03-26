(ns beowulf.reader.macros
  "Can I implement reader macros? let's see!"
  (:require [beowulf.bootstrap :refer [CADR CADDR CDDR CONS]]
            [beowulf.cons-cell :refer [make-beowulf-list]]
            [beowulf.host :refer [LIST]]
            [clojure.string :refer [join]])
  (:import [beowulf.cons_cell ConsCell]))

;; We don't need (at least, in the Clojure reader) to rewrite forms like
;; "'FOO", because that's handled by the parser. But we do need to rewrite
;; things which don't evaluate their arguments, like `SETQ`, because (unless
;; LABEL does it, which I'm not yet sure of) we're not yet able to implement
;; things which don't evaluate arguments.

(def ^:dynamic *readmacros*
  {:car {'DEFUN (fn [f]
                  (LIST 'SET (LIST 'QUOTE (CADR f))
                        (CONS 'LAMBDA (CDDR f))))
         'SETQ (fn [f] (LIST 'SET (LIST 'QUOTE (CADR f)) (CADDR f)))}})

(defn expand-macros
  [form]
  (try
    (if-let [car (when (and (coll? form) (symbol? (first form))) 
                   (first form))]
      (if-let [macro (-> *readmacros* :car car)]
        (make-beowulf-list (apply macro (list form)))
        form)
      form)
    (catch Exception any
      (println (join "\n"
                     ["# ERROR while expanding macro:"
                      (str "# Form: " form)
                      (str "# Error class: " (.getName (.getClass any)))
                      (str "# Message: " (.getMessage any))]))
      form)))