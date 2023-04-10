(ns beowulf.reader-macro-test
  (:require [clojure.test :refer [deftest is testing]]
            [beowulf.read :refer [gsp]]
            [beowulf.reader.macros :refer [expand-macros]]))

(deftest macro-expansion
  (testing "Expanding DEFUN"
    (let [expected "(SET (QUOTE FACT) (QUOTE (LAMBDA (X) (COND ((ZEROP X) 1) (T (TIMES X (FACT (SUB1 X))))))))"
          source "(DEFUN FACT (X) (COND ((ZEROP X) 1) (T (TIMES X (FACT (SUB1 X))))))"
          actual (print-str (gsp source))]
      (is (= actual expected)))))