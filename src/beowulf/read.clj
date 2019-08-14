(ns beowulf.read
  (:require [instaparse.core :as i]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL]])
;;  (:import [beowulf.cons-cell ConsCell])
  )

(def parse
  "Parse a string presented as argument into a parse tree which can then
  be operated upon further."
  (i/parser
    "expr := mexpr | sexpr;
    mexpr := fncall | defn | cond;
    fncall := fn-name lsqb args rsqb;
    lsqb := '[';
    rsqb := ']';
    defn := mexpr opt-space '=' opt-space mexpr;
    cond := lsqb (cond-clause semi-colon opt-space)* cond-clause rsqb;
    cond-clause := expr opt-space arrow opt-space expr;
    arrow := '->';
    args := (arg semi-colon opt-space)* arg;
    arg := mexpr | sexpr;
    fn-name := #'[a-z]*';
    semi-colon := ';';

    list := lpar sexpr rpar | lpar (sexpr sep)* rpar | lpar (sexpr sep)* dot-terminal;
    dotted-pair := lpar dot-terminal ;
    dot := '.';
    lpar := '(';
    rpar := ')';
    sexpr := atom | number | dotted-pair | list;
    dot-terminal := sexpr space dot space sexpr rpar;
    space := #'\\p{javaWhitespace}+';
    opt-space := #'\\p{javaWhitespace}*';
    sep := ',' | opt-space;
    atom := #'[A-Z][A-Z0-9]*';
    number := #'-?[0-9][0-9.]*';"))

(defn simplify
  "Simplify this parse tree `p`."
  [p]
  (if
    (coll? p)
    (apply
      vector
      (remove
        #(if (coll? %) (empty? %))
        (case (first p)
          (:arrow :dot :lpar :lsqb :opt-space :rpar :rsqb :semi-colon :sep :space) nil
          (:arg :expr :mexpr :sexpr) (simplify (second p))
          (:args :cond :cond-clause :dot-terminal :dotted-pair :fncall :list) (map simplify p)
          ;;default
          p)))
    p))

(defn gen-dot-terminated-list
  "Generate a list, which may be dot-terminated, from this partial parse tree
  'p'. Note that the function acts recursively and progressively decapitates
  its argument, so that the argument will not always be a valid parse tree."
  [p]
  (println p)
  (cond
    (empty? p)
    NIL
    (and (coll? (first p)) (= :dot-terminal (first (first p))))
    (let [dt (first p)]
      (make-cons-cell
        (generate (nth dt 1))
        (generate (nth dt 2))))
    :else
    (make-cons-cell
      (generate (first p))
      (gen-dot-terminated-list (rest p)))))

(defn gen-cond-clause
  [p]
  (if
    (and (coll? p)(= :cond-clause (first p)))
    (make-beowulf-list
      (list (generate (nth p 1))
                     (generate (nth p 2))))))



(defn gen-cond
  [p]
  (if
    (and (coll? p)(= :cond (first p)))
    (make-beowulf-list
      (cons
        'COND
        (map
          gen-cond-clause
          (rest p))))))



(defn generate
  "Generate lisp structure from this parse tree `p`. It is assumed that
  `p` has been simplified."
  [p]
  (if
    (coll? p)
    (case (first p)
      :atom (symbol (second p))
      :cond (gen-cond p)
      :dotted-pair (make-cons-cell
                     (generate (nth p 1))
                     (generate (nth p 2)))
      :list (gen-dot-terminated-list (rest p))
      :number (clojure.core/read-string (second p))
      ;; default
      (throw (Exception. (str "Cannot yet generate " (first p)))))
    p))

