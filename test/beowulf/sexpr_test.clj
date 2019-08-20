(ns beowulf.sexpr-test
  (:require [clojure.math.numeric-tower :refer [abs]]
            [clojure.test :refer :all]
            [beowulf.cons-cell :refer :all]
            [beowulf.bootstrap :refer [*options*]]
            [beowulf.read :refer [parse simplify generate gsp]]))

;; broadly, sexprs should be homoiconic

(deftest atom-tests
  (testing "Reading atoms"
    (let [expected 'A
          actual (generate (simplify (parse (str expected))))]
      (is (= actual expected)))
    (let [expected 'APPLE
          actual (generate (simplify (parse (str expected))))]
      (is (= actual expected)))
    (let [expected 'PART2
          actual (generate (simplify (parse (str expected))))]
      (is (= actual expected)))
    (let [expected 'EXTRALONGSTRINGOFLETTERS
          actual (generate (simplify (parse (str expected))))]
      (is (= actual expected)))
    (let [expected 'A4B66XYZ2
          actual (generate (simplify (parse (str expected))))]
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
;;     ;; TODO: Currently failing and I'm not sure why
;;     (binding [*options* {:strict true}]
;;       (is (thrown-with-msg?
;;             Exception
;;             #"Cannot parse comments in strict mode"
;;             (gsp "(A ;; comment
;;                  B C)"))))
    ))


(deftest number-tests
  (testing "Reading octal numbers"
    (let [expected 1
             actual (generate (simplify (parse "1Q")))]
         (is (= actual expected)))
    (let [expected -1
             actual (generate (simplify (parse "-1Q")))]
         (is (= actual expected)))
    (let [expected 8
             actual (generate (simplify (parse "1Q1")))]
         (is (= actual expected)))
    (let [expected -8
             actual (generate (simplify (parse "-1Q1")))]
         (is (= actual expected)))
    (let [expected 128
             actual (generate (simplify (parse "2Q2")))]
         (is (= actual expected)))
    (let [expected 2093056
             actual (generate (simplify (parse "777Q4")))]
         (is (= actual expected))))
  (testing "Reading decimal numbers - broadly should be homiconic"
    (let [expected 7
             actual (generate (simplify (parse "7")))]
         (is (= actual expected)))
    (let [expected -7
             actual (generate (simplify (parse "-7")))]
         (is (= actual expected)))
    (let [expected 3.141592
             actual (generate (simplify (parse "3.141592")))]
         (is (= actual expected)))
    (let [expected 1234567890
             actual (generate (simplify (parse "1234567890")))]
         (is (= actual expected)))
    (let [expected -45.23
             actual (generate (simplify (parse "-45.23")))]
         (is (= actual expected))))
  (testing "Reading scientific notation")
    (let [expected 2/5
             actual (generate (simplify (parse "4E-1")))]
         (is (< (abs (- actual expected)) 0.0001)))
    (let [expected 60
             actual (generate (simplify (parse "6E1")))]
         (is (< (abs (- actual expected)) 0.0001)))
    (let [expected 60
             actual (generate (simplify (parse "600.00E-1")))]
         (is (< (abs (- actual expected)) 0.0001)))
    (let [expected 60
             actual (generate (simplify (parse "0.6E2")))]
         (is (< (abs (- actual expected)) 0.0001))))

(deftest dotted-pair-tests
  (testing "Reading dotted pairs"
    (let [expected "(A . B)"
          actual (print-str (generate (simplify (parse expected))))]
      (is (= actual expected)))
    (let [expected "(A B C . D)"
          actual (print-str (generate (simplify (parse expected))))]
      (is (= actual expected)))
    (let [expected "(A B (C . D) E)"
          actual (print-str (generate (simplify (parse expected))))]
      (is (= actual expected)))))

(deftest list-tests
  (testing "Reading arbitrarily structured lists"
    (let [expected "(DEFUN FACT (X) (COND ((ZEROP X) 1) (T (TIMES X (FACT (SUB1 X))))))"
          actual (print-str (generate (simplify (parse expected))))]
      (is (= actual expected)))))
