(ns beowulf.bootstrap-test
  (:require [clojure.math.numeric-tower :refer [abs]]
            [clojure.test :refer :all]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL T F]]
            [beowulf.eval :refer :all]
            [beowulf.read :refer [parse simplify generate]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; This file is primarily tests of the functions in `beowulf.eval` - which
;;; are Clojure functions, but aim to provide sufficient functionality that
;;; Beowulf can get up to the level of running its own code.
;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest atom-tests
  (testing "primitive-atom"
    (let [expected T
          actual (primitive-atom T)]
      (is (= actual expected) "T is an atom (symbol)"))
    (let [expected T
          actual (primitive-atom (generate (simplify (parse "HELLO"))))]
      (is (= actual expected) "HELLO is an atom (symbol)"))
    (let [expected T
          actual (primitive-atom 7)]
      (is (= actual expected)
          "I'm not actually certain whether a number should be treated as an
          atom, but I'm guessing so"))
    (let [expected F
          actual (primitive-atom (make-cons-cell 'A 'B))]
      (is (= actual expected) "A dotted pair is explicitly not an atom."))
    (let [expected F
          actual (primitive-atom (generate (simplify (parse "(A B C D)"))))]
      (is (= actual expected) "A list is explicitly not an atom")))
  (testing "primitive-atom?"
    (let [expected T
          actual (primitive-atom? T)]
      (is (= actual expected) "T is an atom (symbol)"))
    (let [expected T
          actual (primitive-atom? (generate (simplify (parse "HELLO"))))]
      (is (= actual expected) "HELLO is an atom (symbol)"))
    (let [expected T
          actual (primitive-atom? 7)]
      (is (= actual expected)
          "I'm not actually certain whether a number should be treated as an
          atom, but I'm guessing so"))
    (let [expected NIL
          actual (primitive-atom? (make-cons-cell 'A 'B))]
      (is (= actual expected) "A dotted pair is explicitly not an atom."))
    (let [expected NIL
          actual (primitive-atom? (generate (simplify (parse "(A B C D)"))))]
      (is (= actual expected) "A list is explicitly not an atom"))

    ))

(deftest access-function-tests
  (testing "car"
    (let [expected 'A
          actual (car (make-cons-cell 'A 'B))]
      (is (= actual expected) "A is car of (A . B)"))
    (let [expected 'A
          actual (car (generate (simplify (parse "(A B C D)"))))]
      (is (= actual expected) "A is car of (A B C D)"))
    (is (thrown-with-msg?
          Exception
          #"Cannot take CAR of `.*"
          (car 'T))
        "Can't take the car of an atom")
    (is (thrown-with-msg?
          Exception
          #"Cannot take CAR of `.*"
          (car 7))
        "Can't take the car of a number"))
  (testing "cdr"
    (let [expected 'B
          actual (cdr (make-cons-cell 'A 'B))]
      (is (= actual expected) "B is cdr of (A . B)"))
    (let [expected 'B
          actual (cdr (generate (simplify (parse "(A B C D)"))))]
      (is (instance? beowulf.cons_cell.ConsCell actual)
          "cdr of (A B C D) is a cons cell")
      (is (= (car actual) expected) "the car of that cons-cell is B"))
    (is (thrown-with-msg?
          Exception
          #"Cannot take CDR of `.*"
          (cdr 'T))
        "Can't take the cdr of an atom")
    (is (thrown-with-msg?
          Exception
          #"Cannot take CDR of `.*"
          (cdr 7))
        "Can't take the cdr of a number"))
  (let [s (generate
            (simplify
              (parse
                "((((1 . 2) 3)(4 5) 6)(7 (8 9) (10 11 12) 13) 14 (15 16) 17)")))]
    ;; structure for testing access functions
    (testing "cadr"
      (let [expected 'B
            actual (cadr (generate (simplify (parse "(A B C D)"))))]
        (is (= actual expected))))
    (testing "caddr"
      (let [expected 'C
            actual (caddr (generate (simplify (parse "(A B C D)"))))]
        (is (= actual expected))))
    (testing "cadddr"
      (let [expected 'D
            actual (cadddr (generate (simplify (parse "(A B C D)"))))]
        (is (= actual expected))))
    (testing "caaaar"
      (let [expected "1"
            actual (print-str (caaaar s))]
        (is (= actual expected))))
    ))


