(ns beowulf.io
  "Non-standard extensions to Lisp 1.5 to read and write to the filesystem.
   
   Lisp 1.5 had only `READ`, which read one S-Expression at a time, and 
   various forms of `PRIN*` functions, which printed to the line printer. 
   There was also `PUNCH`, which wrote to a card punch. It does not seem 
   that there was any concept of an interactive terminal.
   
   See Appendix E, `OVERLORD - THE MONITOR`, and Appendix F, `LISP INPUT
   AND OUTPUT`.
   
   For our purposes, to save the current state of the Lisp system it should
   be sufficient to print the current contents of the oblist to file; and to
   restore a previous state from file, to overwrite the contents of the 
   oblist with data from that file.
   
   Hence functions SYSOUT and SYSIN, which do just that."
  (:require [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell
                                       pretty-print]]
            [beowulf.host :refer [CADR CAR CDDR CDR]]
            [beowulf.interop :refer [interpret-qualified-name
                                     listify-qualified-name]]
            [beowulf.oblist :refer [*options* NIL oblist]]
            [beowulf.read :refer [READ]]
            [clojure.java.io :refer [file resource]]
            [clojure.string :refer [ends-with?]]
            [java-time.api :refer [local-date local-date-time]]))

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

(def ^:constant default-sysout "resources/lisp1.5.lsp")

(defn- full-path
  [fp]
  (str
   (if (:filepath *options*)
     (str (:filepath *options*) (java.io.File/separator))
     "")
   (if (and (string? fp)
            (> (count fp) 0)
            (not= fp "NIL"))
     fp
     (str "Sysout-" (local-date)))
   (if (ends-with? fp ".lsp")
     ""
     ".lsp")))

;; (find-var (symbol "beowulf.io/SYSIN"))
;; (@(resolve (symbol "beowulf.host/TIMES")) 2 2)

(defn safely-wrap-subr
  [entry]
  (cond (= entry NIL) NIL
        (= (CAR entry) 'SUBR) (make-cons-cell
                               (CAR entry)
                               (make-cons-cell
                                (listify-qualified-name (CADR entry))
                                (CDDR entry)))
        :else (make-cons-cell
               (CAR entry) (safely-wrap-subr (CDR entry)))))

(defn safely-wrap-subrs
  [objects]
  (make-beowulf-list (map safely-wrap-subr objects)))

(defn SYSOUT
  "Dump the current content of the object list to file. If no `filepath` is
   specified, a file name will be constructed of the symbol `Sysout` and 
   the current date. File paths will be considered relative to the filepath
   set when starting Lisp.
   
   **NOTE THAT** this is an extension function, not available in strct mode."
  ([]
   (SYSOUT nil))
  ([filepath]
   (spit (full-path (str filepath))
         (with-out-str
           (println (apply str (repeat 79 ";")))
           (println (format ";; Beowulf %s Sysout file generated at %s"
                            (or (System/getProperty "beowulf.version") "")
                            (local-date-time)))
           (when (System/getenv "USER")
             (println (format ";; generated by %s" (System/getenv "USER"))))
           (println (apply str (repeat 79 ";")))
           (println)
           (let [output (safely-wrap-subrs @oblist)]
             (pretty-print output)
             )))))

(defn- resolve-subr
  "If this oblist `entry` references a subroutine, attempt to fix up that
   reference."
  [entry]
  (cond (= entry NIL) NIL
        (= (CAR entry) 'SUBR) (try
                                (make-cons-cell
                                 (CAR entry)
                                 (make-cons-cell
                                  (interpret-qualified-name
                                         (CADR entry))
                                  (CDDR entry)))
                                (catch Exception _
                                  (print "Warning: failed to resolve "
                                         (CADR entry))
                                  (CDDR entry)))
        :else (make-cons-cell
               (CAR entry) (resolve-subr (CDR entry)))))


(defn- resolve-subroutines
  "Attempt to fix up the references to subroutines (Clojure functions) among
   these `objects`, being new content for the object list."
  [objects]
  (make-beowulf-list
   (map
    resolve-subr
    objects)))

(defn SYSIN
  "Read the contents of the file at this `filename` into the object list. 
   
   If the file is not a valid Beowulf sysout file, this will probably 
   corrupt the system, you have been warned. File paths will be considered 
   relative to the filepath set when starting Lisp.

   It is intended that sysout files can be read both from resources within
   the jar file, and from the file system. If a named file exists in both the
   file system and the resources, the file system will be preferred.
   
   **NOTE THAT** if the provided `filename` does not end with `.lsp` (which,
   if you're writing it from the Lisp REPL, it won't), the extension `.lsp`
   will be appended.
   
   **NOTE THAT** this is an extension function, not available in strct mode."
  ([]
   (SYSIN (or (:read *options*) default-sysout)))
  ([filename]
   (let [fp (file (full-path (str filename)))
         file (when (and (.exists fp) (.canRead fp)) fp)
         res (try (resource filename)
                  (catch Throwable _ nil))
         content (try (READ (slurp (or file res)))
                      (catch Throwable any
                        (throw (ex-info "Could not read from file"
                                        {:context "SYSIN"
                                         :filepath fp}
                                        any))))]
     (swap! oblist
            #(when (or % (seq content))
               (resolve-subroutines content))))))
