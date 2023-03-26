(ns beowulf.read
  "This provides the reader required for boostrapping. It's not a bad
  reader - it provides feedback on errors found in the input - but it isn't
  the real Lisp reader.

  Intended deviations from the behaviour of the real Lisp reader are as follows:

  1. It reads the meta-expression language `MEXPR` in addition to fLAMthe
      symbolic expression language `SEXPR`, which I do not believe the Lisp 1.5
      reader ever did;
  2. It treats everything between a semi-colon and an end of line as a comment,
      as most modern Lisps do; but I do not believe Lisp 1.5 had this feature.

  Both these extensions can be disabled by using the `--strict` command line
  switch."
  (:require [beowulf.reader.generate :refer [generate]]
            [beowulf.reader.parser :refer [parse]]
            [beowulf.reader.simplify :refer [remove-optional-space simplify]]
            [clojure.string :refer [join split starts-with? trim]])
  (:import [java.io InputStream]
           [instaparse.gll Failure]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; This file provides the reader required for boostrapping. It's not a bad
;;; reader - it provides feedback on errors found in the input - but it isn't
;;; the real Lisp reader.
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
             (throw (ex-info "Parse failed" (assoc parse-tree :source source))))
      (generate (simplify (remove-optional-space parse-tree))))))

(defn read-from-console
  "Attempt to read a complete lisp expression from the console."
  []
  (loop [r (read-line)]
    (if (= (count (re-seq #"\(" r))
           (count (re-seq #"\)" r)))
      r
      (recur (str r "\n" (read-line))))))

(defn READ
  "An implementation of a Lisp reader sufficient for bootstrapping; not necessarily
  the final Lisp reader. `input` should be either a string representation of a LISP
  expression, or else an input stream. A single form will be read."
  ([]
   (gsp (read-from-console)))
  ([input]
   (cond
     (empty? input) (gsp (read-from-console))
     (string? input) (gsp input)
     (instance? InputStream input) (READ (slurp input))
     :else    (throw (ex-info "READ: `input` should be a string or an input stream" {})))))
