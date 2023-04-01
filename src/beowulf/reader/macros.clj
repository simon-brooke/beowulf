(ns beowulf.reader.macros
  "Can I implement reader macros? let's see!
   
   We don't need (at least, in the Clojure reader) to rewrite forms like
   `'FOO`, because that's handled by the parser. But we do need to rewrite
   things which don't evaluate their arguments, like `SETQ`, because (unless
   LABEL does it, which I'm not yet sure of) we're not yet able to implement
   things which don't evaluate arguments.

   TODO: at this stage, the following should probably also be read macros:
   DEFINE"
  (:require [beowulf.cons-cell :refer [make-beowulf-list]]
            [beowulf.host :refer [CONS LIST]]
            [clojure.string :refer [join]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; We don't need (at least, in the Clojure reader) to rewrite forms like
;;; "'FOO", because that's handled by the parser. But we do need to rewrite
;;; things which don't evaluate their arguments, like `SETQ`, because (unless
;;; LABEL does it, which I'm not yet sure of) we're not yet able to implement
;;; things which don't evaluate arguments.
;;;
;;; TODO: at this stage, the following should probably also be read macros:
;;; DEFINE
;;;
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

(def ^:dynamic *readmacros*
  {:car {'DEFUN (fn [f]
                  (LIST 'SET (LIST 'QUOTE (second f))
                        (LIST 'QUOTE (CONS 'LAMBDA (rest (rest f))))))
         'SETQ (fn [f] (LIST 'SET (LIST 'QUOTE (second f)) (LIST 'QUOTE (nth f 2))))}})

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