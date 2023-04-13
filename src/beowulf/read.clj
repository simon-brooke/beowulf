(ns beowulf.read
  "This provides the reader required for boostrapping. It's not a bad
  reader - it provides feedback on errors found in the input - but it isn't
  the real Lisp reader.

  Intended deviations from the behaviour of the real Lisp reader are as follows:

  1. It reads the meta-expression language `MEXPR` in addition to the
      symbolic expression language `SEXPR`, which I do not believe the Lisp 1.5
      reader ever did;
  2. It treats everything between a double semi-colon and an end of line as a comment,
      as most modern Lisps do; but I do not believe Lisp 1.5 had this feature.

  Both these extensions can be disabled by using the `--strict` command line
  switch."
  (:require [beowulf.oblist :refer [*options*]]
            [beowulf.reader.char-reader :refer [read-chars]]
            [beowulf.reader.generate :refer [generate]]
            [beowulf.reader.parser :refer [parse]]
            [beowulf.reader.simplify :refer [simplify]]
            [clojure.string :refer [join split starts-with? trim]])
  (:import [instaparse.gll Failure]
           [java.io InputStream]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; This file provides the reader required for boostrapping. It's not a bad
;;; reader - it provides feedback on errors found in the input - but it isn't
;;; the real Lisp reader.
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

(defn strip-line-comments
  "Strip blank lines and comment lines from this string `s`, expected to
   be Lisp source."
  [^String s]
  (join "\n"
        (remove
         #(or (empty? %)
              (starts-with? (trim %) ";;"))
         (split s #"\n"))))

(defn number-lines
  ([^String s]
   (number-lines s nil))
  ([^String s ^Failure e]
   (let [l (-> e :line)
         c (-> e :column)]
     (join "\n"
           (map #(str (format "%5d %s" (inc %1) %2)
                      (when (= l (inc %1))
                        (str "\n" (apply str (repeat c " ")) "^")))
                (range)
                (split s #"\n"))))))

(defn gsp
  "Shortcut macro - the internals of read; or, if you like, read-string.
  Argument `s` should be a string representation of a valid Lisp
  expression."
  [s]
  (let [source (strip-line-comments s)
        parse-tree (parse source)]
    (if (instance? Failure parse-tree)
      (doall (println (number-lines source parse-tree))
             (throw (ex-info "Ne can forstande " (assoc parse-tree :source source))))
      (generate (simplify parse-tree)))))

(defn- dummy-read-chars [prompt]
  (loop [r "" p prompt]
    (if (and (seq r)
             (= (count (re-seq #"\(" r))
                (count (re-seq #"\)" r)))
             (= (count (re-seq #"\[" r))
                (count (re-seq #"\]" r))))
      r
      (do
        (print (str p " "))
        (flush)
        (recur (str r "\n" (read-line)) "::")))))

(defn read-from-console
  "Attempt to read a complete lisp expression from the console.
   
   There's a major problem here that the read-chars reader messes up testing.
   We need to be able to disable it while testing!"
  [prompt]
  (if (:testing *options*)
    (dummy-read-chars prompt)
    (read-chars prompt)))

(defn READ
  "An implementation of a Lisp reader sufficient for bootstrapping; not necessarily
  the final Lisp reader. `input` should be either a string representation of a LISP
  expression, or else an input stream. A single form will be read."
  ([]
   (gsp (read-from-console (:prompt *options*))))
  ([input]
   (cond
     (empty? input) (READ)
     (string? input) (gsp input)
     (instance? InputStream input) (READ (slurp input))
     :else    (throw (ex-info "READ: `input` should be a string or an input stream" {})))))
