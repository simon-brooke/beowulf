(ns beowulf.interop
  (:require [beowulf.cons-cell :refer [make-beowulf-list]]
            [beowulf.host :refer [CAR CDR]]
            [beowulf.oblist :refer [*options* NIL]]
            [clojure.string :as s :refer [last-index-of lower-case split
                                          upper-case]]))

;;;; INTEROP feature ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn listify-qualified-name
  "We need to be able to print something we can link to the particular Clojure
   function `subr` in a form in which Lisp 1.5 is able to read it back in and
   relink it.
   
   This assumes `subr` is either 
   1. a string in the format `#'beowulf.io/SYSIN` or `beowulf.io/SYSIN`; or
   2. something which, when coerced to a string with `str`, will have such
      a format."
  [subr]
  (make-beowulf-list
   (map
    #(symbol (upper-case %))
    (remove empty? (split (str subr) #"[#'./]")))))


(defn interpret-qualified-name
  "For interoperation with Clojure, it will often be necessary to pass
  qualified names that are not representable in Lisp 1.5. This function
  takes a sequence in the form `(PART PART PART... NAME)` and returns
  a symbol in the form `part.part.part/NAME`. This symbol will then be
  tried in both that form and lower-cased. Names with hyphens or
  underscores cannot be represented with this scheme."
  ([l]
   (symbol
    (let [n (s/join "." 
                    (concat (map #(lower-case (str %)) (butlast l)) 
                            (list (last l))))
          s (last-index-of n ".")]
      (if s
        (str (subs n 0 s) "/" (subs n (inc s)))
        n)))))

(defn to-beowulf
  "Return a beowulf-native representation of the Clojure object `o`.
  Numbers and symbols are unaffected. Collections have to be converted;
  strings must be converted to symbols."
  [o]
  (cond
    (coll? o) (make-beowulf-list o)
    (string? o) (symbol (s/upper-case o))
    :else o))

(defn to-clojure
  "If l is a `beowulf.cons_cell.ConsCell`, return a Clojure list having the 
  same members in the same order."
  [l]
  (cond
    (not (instance? beowulf.cons_cell.ConsCell l))
    l
    (= (CDR l) NIL)
    (list (to-clojure (CAR l)))
    :else
    (conj (to-clojure (CDR l)) (to-clojure (CAR l)))))

(defn INTEROP
  "Clojure (or other host environment) interoperation API. `fn-symbol` is expected
  to be either

  1. a symbol bound in the host environment to a function; or
  2. a sequence (list) of symbols forming a qualified path name bound to a
     function.

  Lower case characters cannot normally be represented in Lisp 1.5, so both the
  upper case and lower case variants of `fn-symbol` will be tried. If the
  function you're looking for has a mixed case name, that is not currently
  accessible.

  `args` is expected to be a Lisp 1.5 list of arguments to be passed to that
  function. Return value must be something acceptable to Lisp 1.5, so either
  a symbol, a number, or a Lisp 1.5 list.

  If `fn-symbol` is not found (even when cast to lower case), or is not a function,
  or the value returned cannot be represented in Lisp 1.5, an exception is thrown
  with `:cause` bound to `:interop` and `:detail` set to a value representing the
  actual problem."
  [fn-symbol args]
  (if-not (:strict *options*)
    (let
     [q-name (if
              (seq? fn-symbol)
               (interpret-qualified-name fn-symbol)
               fn-symbol)
      l-name (symbol (s/lower-case q-name))
      f      (cond
               (try
                 (fn? (eval l-name))
                 (catch java.lang.ClassNotFoundException _ nil)) l-name
               (try
                 (fn? (eval q-name))
                 (catch java.lang.ClassNotFoundException _ nil)) q-name
               :else (throw
                      (ex-info
                       (str "INTEROP: ungecnáwen þegnung `" fn-symbol "`")
                       {:cause      :interop
                        :detail     :not-found
                        :name       fn-symbol
                        :also-tried l-name})))
      args'  (to-clojure args)]
;;      (print (str "INTEROP: eahtiende `" (cons f args') "`"))
      (flush)
      (let [result (eval (conj args' f))] ;; this has the potential to blow up the world
;;        (println (str "; ágiefende `" result "`"))
        (cond
          (instance? beowulf.cons_cell.ConsCell result) result
          (coll? result) (make-beowulf-list result)
          (symbol? result) result
          (string? result) (symbol result)
          (number? result) result
          :else (throw
                 (ex-info
                  (str "INTEROP: Ne can eahtiende `" result "` to Lisp 1.5.")
                  {:cause  :interop
                   :detail :not-representable
                   :result result})))))
    (throw
     (ex-info
      (str "INTEROP ne āfand innan Lisp 1.5.")
      {:cause  :interop
       :detail :strict}))))
