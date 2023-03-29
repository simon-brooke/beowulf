(ns beowulf.reader.simplify
  "Simplify parse trees. Be aware that this is very tightly coupled
   with the parser."
  (:require [beowulf.oblist :refer [*options*]]
            [instaparse.failure :as f])
  (:import [instaparse.gll Failure]))

(declare simplify)

(defn remove-optional-space
  [tree]
  (if (vector? tree)
    (if (= :opt-space (first tree))
      nil
      (let [v (remove nil?
                      (map remove-optional-space tree))]
        (if (seq v)
          (apply vector v)
          v)))
    tree))

(defn remove-nesting
  [tree context]
  (let [tree' (remove-optional-space tree)]
    (if-let [key (when (and (vector? tree') 
                            (keyword? (first tree'))) 
                   (first tree'))]
      (loop [r tree']
        (if (and r (vector? r) (keyword? (first r)))
          (if (= (first r) key)
            (recur (simplify (second r) context))
            r)
          r))
      tree')))

(defn simplify
  "Simplify this parse tree `p`. If `p` is an instaparse failure object, throw
   an `ex-info`, with `p` as the value of its `:failure` key.
   
   **NOTE THAT** it is assumed that `remove-optional-space` has been run on the
   parse tree **BEFORE** it is passed to `simplify`."
  ([p]
   (if
    (instance? Failure p)
     (throw (ex-info
             (str "Ic ne behæfd: " (f/pprint-failure p))
             {:cause :parse-failure
              :phase   :simplify
              :failure p}))
     (simplify p :expr)))
  ([p context]
   (cond
     (string? p) p
     (coll? p) (apply
                vector
                (remove
                 #(when (coll? %) (empty? %))
                 (case (first p)
                   (:λexpr
                    :args :bindings :body :cond :cond-clause :defn :dot-terminal 
                    :fncall :lhs :quoted-expr :rhs ) (map #(simplify % context) p)
                   (:arg :expr :coefficient :fn-name :number) (simplify (second p) context)
                   (:arrow :dot :e :lpar :lsqb  :opt-comment :opt-space :q :quote :rpar :rsqb
                           :semi-colon :sep :space) nil
                   :atom (if
                          (= context :mexpr)
                           [:quoted-expr p]
                           p)
                   :comment (when
                             (:strict *options*)
                              (throw
                               (ex-info "Cannot parse comments in strict mode"
                                        {:cause :strict})))
                   (:decimal :integer :mconst :octal :scientific) p
                   :dotted-pair (if
                                 (= context :mexpr)
                                  [:fncall
                                   [:mvar "cons"]
                                   [:args
                                    (simplify (nth p 1) context)
                                    (simplify (nth p 2) context)]]
                                  (map #(simplify % context) p))
                   :iexp (simplify (second p) context)
                   :iexpr [:iexpr
                           [:lhs (simplify (second p) context)]
                           (simplify (nth p 2) context) ;; really should be the operator
                           [:rhs (simplify (nth p 3) context)]]
                   :mexpr (if
                           (:strict *options*)
                            (throw
                             (ex-info "Cannot parse meta expressions in strict mode"
                                      {:cause :strict}))
                            (simplify (second p) :mexpr))
                   :list (if
                          (= context :mexpr)
                           [:fncall
                            [:mvar "list"]
                            [:args (apply vector (map simplify (rest p)))]]
                           (map #(simplify % context) p))
                   :raw (first (remove empty? (map simplify (rest p))))
                   :sexpr (simplify (second p) :sexpr)
          ;;default
                   p)))
     :else p)))
