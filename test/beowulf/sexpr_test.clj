(ns beowulf.sexpr-test
  (:require [clojure.math.numeric-tower :refer [abs]]
            [clojure.test :refer :all]
            [beowulf.read :refer [parse simplify generate]]
            [beowulf.print :refer :all]))

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

