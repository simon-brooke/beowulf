(ns beowulf.interop-test
  (:require [clojure.test :refer :all]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL T F]]
            [beowulf.bootstrap :refer [EVAL INTEROP]]
            [beowulf.host :refer :all]
            [beowulf.read :refer [gsp]]))


(deftest interop-test
  (testing "INTEROP called from Clojure"
    (let [expected (symbol "123")
          actual (INTEROP (gsp "(CLOJURE CORE STR)") (gsp "(1 2 3)"))]
      (is (= actual expected))))
  (testing "INTEROP called from Lisp"
    (let [expected 'ABC
          actual (EVAL (INTEROP '(CLOJURE CORE STR) '('A 'B 'C)) '())]
      (is (= actual expected))))
    )
