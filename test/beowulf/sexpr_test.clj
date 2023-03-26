(ns beowulf.sexpr-test
  (:require [clojure.test :refer [deftest is testing]]
            [beowulf.bootstrap :refer [*options*]]
            [beowulf.cons-cell :refer []]
            [beowulf.read :refer [gsp]]))

;; broadly, sexprs should be homoiconic

(deftest atom-tests
  (testing "Reading atoms"
    (let [expected 'A
          actual (gsp (str expected))]
      (is (= actual expected)))
    (let [expected 'APPLE
          actual (gsp (str expected))]
      (is (= actual expected)))
    (let [expected 'PART2
          actual (gsp (str expected))]
      (is (= actual expected)))
    (let [expected 'EXTRALONGSTRINGOFLETTERS
          actual (gsp (str expected))]
      (is (= actual expected)))
    (let [expected 'A4B66XYZ2
          actual (gsp (str expected))]
      (is (= actual expected)))))

(deftest comment-tests
  (testing "Reading comments"
    (let [expected 'A
          actual (gsp "A ;; comment")]
      (is (= actual expected)))
    (let [expected 10
          actual (gsp "10 ;; comment")]
      (is (= actual expected)))
    (let [expected 2/5
          actual (gsp "4E-1 ;; comment")]
      (is (= actual expected)))
    (let [expected "(A B C)"
          actual (print-str (gsp "(A ;; comment
                      B C)"))]
      (is (= actual expected)
          "Really important that comments work inside lists"))
    ;;  ;; TODO: Currently failing and I'm not sure why
    ;;  (binding [*options* {:strict true}]
    ;;    (is (thrown-with-msg?
    ;;          Exception
    ;;          #"Cannot parse comments in strict mode"
    ;;          (gsp "(A ;; comment
    ;;               B C)"))))
    ))


(deftest number-tests
  (testing "Reading octal numbers"
    (let [expected 1
             actual (gsp "1Q")]
         (is (= actual expected)))
    (let [expected -1
             actual (gsp "-1Q")]
         (is (= actual expected)))
    (let [expected 8
             actual (gsp "1Q1")]
         (is (= actual expected)))
    (let [expected -8
             actual (gsp "-1Q1")]
         (is (= actual expected)))
    (let [expected 128
             actual (gsp "2Q2")]
         (is (= actual expected)))
    (let [expected 2093056
             actual (gsp "777Q4")]
         (is (= actual expected))))
  (testing "Reading decimal numbers - broadly should be homiconic"
    (let [expected 7
             actual (gsp "7")]
         (is (= actual expected)))
    (let [expected -7
             actual (gsp "-7")]
         (is (= actual expected)))
    (let [expected 3.141592
             actual (gsp "3.141592")]
         (is (= actual expected)))
    (let [expected 1234567890
             actual (gsp "1234567890")]
         (is (= actual expected)))
    (let [expected -45.23
             actual (gsp "-45.23")]
         (is (= actual expected))))
  (testing "Reading scientific notation")
    (let [expected 2/5
             actual (gsp "4E-1")]
         (is (< (abs (- actual expected)) 0.0001)))
    (let [expected 60
             actual (gsp "6E1")]
         (is (< (abs (- actual expected)) 0.0001)))
    (let [expected 60
             actual (gsp "600.00E-1")]
         (is (< (abs (- actual expected)) 0.0001)))
    (let [expected 60
             actual (gsp "0.6E2")]
         (is (< (abs (- actual expected)) 0.0001))))

(deftest dotted-pair-tests
  (testing "Reading dotted pairs"
    (let [expected "(A . B)"
          actual (print-str (gsp expected))]
      (is (= actual expected)))
    (let [expected "(A B C . D)"
          actual (print-str (gsp expected))]
      (is (= actual expected)))
    (let [expected "(A B (C . D) E)"
          actual (print-str (gsp expected))]
      (is (= actual expected)))))

(deftest list-tests
  (testing "Reading arbitrarily structured lists"
    (let [expected "(SET (QUOTE FACT) (LAMBDA (X) (COND ((ZEROP X) 1) (T (TIMES X (FACT (SUB1 X)))))))"
          actual (print-str (gsp expected))]
      (is (= actual expected)))))
