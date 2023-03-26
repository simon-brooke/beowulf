(ns beowulf.interop-test
  (:require [clojure.test :refer [deftest is testing]]
            [beowulf.bootstrap :refer [EVAL INTEROP QUOTE]]
            [beowulf.read :refer [gsp]]))


(deftest interop-test
  (testing "INTEROP called from Clojure"
    (let [expected (symbol "123")
          actual (INTEROP (gsp "(CLOJURE CORE STR)") (gsp "(1 2 3)"))]
      (is (= actual expected))))
  ;; (testing "INTEROP called from Lisp"
  ;;   (let [expected 'ABC
  ;;         actual (EVAL (gsp "(INTEROP '(CLOJURE CORE STR) '(A B C))") (gsp "((A . A)(B . B)(C . C))"))]
  ;;     (is (= actual expected))))
    )
