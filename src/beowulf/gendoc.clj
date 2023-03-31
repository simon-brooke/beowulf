(ns beowulf.gendoc
  "Generate table of documentation of Lisp symbols and functions.
   
   NOTE: this is *very* hacky. You almost certainly do not want to 
   use this!"
  (:require [beowulf.io :refer [default-sysout SYSIN]]
            [beowulf.oblist :refer [oblist]]
            [clojure.string :refer [join replace upper-case]]))

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

(def host-functions
  "Functions which we can infer are written in Clojure."
  (reduce
   merge
   {}
   (map
    ns-publics
    ['beowulf.bootstrap
     'beowulf.host
     'beowulf.io
     'beowulf.read])))

;; OK, this, improbably, works. There's probably a better way...
;; (:doc (meta (eval (read-string (str "#'" "beowulf.read" "/" "READ")))))

(defn- get-metadata-for-function
  "Return the metadata associated with this compiled Clojure `function`. 
   
   If `key` is passed, return only the value of `key` in that metadata.
   The value of `key` should be a keyword; of `function`, a function."
  ([function]
   (try
     (meta (eval (read-string (str function))))
     (catch Throwable _ "?")))
  ([function key]
   (when (keyword? key)
     (key (get-metadata-for-function function)))))


(defn- get-metadata-for-entry [entry key]
  (let [fn (host-functions (symbol (first entry)))]
    (get-metadata-for-function fn key)))

(defn infer-type
  "Try to work out what this `entry` from the oblist actually
   represents."
  [entry]
  (cond
    (= (second entry) 'LAMBDA) "Lisp function"
    (= (second entry) 'LABEL) "Labeled form"
    (host-functions (first entry)) (if (fn? (eval (symbol (host-functions (first entry)))))
                                     "Host function"
                                     "Host variable")
    :else "Lisp variable"))

(defn- format-clj-signature
  "Format the signature of the Clojure function represented by `symbol` for
   Lisp documentation."
  [symbol arglists]
  (join
   "; "
   (doall
    (map
     (fn [l]
       (join (concat (list "("  symbol " ")
                     (join " " (map #(upper-case (str %)) l)) (list ")"))))
     arglists))))

(defn infer-signature
  "Infer the signature of the function value of this oblist `entry`, if any."
  [entry]
  (cond
    (= (count entry) 1) (format-clj-signature
                         (first entry)
                         (get-metadata-for-entry entry :arglists))
    (= (second entry) 'LAMBDA) (str (cons (first entry) (nth entry 2)))
    :else "?"))

(defn find-documentation
  "Find appropriate documentation for this `entry` from the oblist."
  [entry]
  (cond
    (= (count entry) 1) (if-let [doc (get-metadata-for-entry entry :doc)]
                          (replace doc "\n" " ")
                          "?")
    :else "?"))

(defn gen-doc-table
  ([]
   (gen-doc-table default-sysout))
  ([sysfile]
   (try (SYSIN sysfile)
        (catch Throwable any
          (println (.getMessage any) " while reading " sysfile)))
   (join
    "\n"
    (doall
     (concat
      '("| Symbol | Type | Signature | Documentation |"
        "|--------|------|-----------|---------------|")
      (map
       #(format "| %s | %s | %s | %s |"
                (first %)
                (infer-type %)
                (infer-signature %)
                (find-documentation %))
       @oblist))))))

;; (println (gen-doc-table))