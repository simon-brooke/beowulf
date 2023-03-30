(ns beowulf.gendoc
  (:require [beowulf.oblist :refer [oblist]]
            [clojure.string :refer [join replace]]))

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
  [entry]
  (cond
    (= (second entry) 'LAMBDA) "Lisp function"
    (host-functions (first entry)) "Host function"
    :else "?"))

(defn infer-signature
  [entry]
  (cond
    (= (count entry) 1) (get-metadata-for-entry entry :arglists)
    (= (second entry) 'LAMBDA) (nth entry 2)
    :else "?"))

(defn find-documentation
  [entry]
  (cond
    (= (count entry) 1) (if-let [doc (get-metadata-for-entry entry :doc)]
                          (replace doc "\n" " ")
                          "?")
    :else "?"))

(defn gen-doc-table
  []
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
      @oblist)))))

;; (println (gen-doc-table))