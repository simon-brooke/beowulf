(ns beowulf.reader.simplify
  "Simplify parse trees. Be aware that this is very tightly coupled
   with the parser."
  (:require [beowulf.bootstrap :refer [*options*]]
            [instaparse.failure :as f])
   (:import [instaparse.gll Failure]))

(declare simplify)

(defn remove-optional-space
  [tree]
  (if (vector? tree)
    (if (= :opt-space (first tree))
      nil
      (remove nil?
              (map remove-optional-space tree)))
    tree))

(defn remove-nesting
  [tree]
  (let [tree' (remove-optional-space tree)]
    (if-let [key (when (and (vector? tree') (keyword? (first tree'))) (first tree'))]
      (loop [r tree']
        (if (and r (vector? r) (keyword? (first r)))
          (if (= (first r) key)
            (recur (simplify (second r) :foo))
            r)
          r))
      tree')))


(defn simplify
  "Simplify this parse tree `p`. If `p` is an instaparse failure object, throw
  an `ex-info`, with `p` as the value of its `:failure` key."
  ([p]
   (if
    (instance? Failure p)
     (throw (ex-info (str "Ic ne behæfd: " (f/pprint-failure p)) {:cause   :parse-failure
                                                                  :phase   :simplify
                                                                  :failure p}))
     (simplify p :expr)))
  ([p context]
   (if
    (coll? p)
     (apply
      vector
      (remove
       #(when (coll? %) (empty? %))
       (case (first p)
         (:λexpr
          :args :bindings :body :cond :cond-clause :defn :dot-terminal
          :fncall :lhs :octal :quoted-expr :rhs :scientific) (map #(simplify % context) p)
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
         :dotted-pair (if
                       (= context :mexpr)
                        [:fncall
                         [:mvar "cons"]
                         [:args
                          (simplify (nth p 1) context)
                          (simplify (nth p 2) context)]]
                        (map simplify p))
         :iexp (second (remove-nesting p))
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
     p)))
