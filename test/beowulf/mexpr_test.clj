(ns beowulf.mexpr-test
  (:require [clojure.test :refer :all]
            [beowulf.read :refer [parse simplify generate]]
            [beowulf.print :refer :all]))

;; These tests are taken generally from the examples on page 10 of
;; Lisp 1.5 Programmers Manual:

;; ## Examples

;; M-expressions                                S-expressions
;; x                                            X
;; car                                          CAR
;; car[x]                                       (CAR X)
;; T                                            (QUOTE T)
;; ff[car [x]]                                  (FF (CAR X))
;; [atom[x]->x; T->ff[car[x]]]                  (COND ((ATOM X) X)
;;                                                ((QUOTE T)(FF (CAR X))))
;; label[ff;λ[[x];[atom[x]->x; T->ff[car[x]]]]] (LABEL FF (LAMBDA (X) (COND
;;                                                ((ATOM X) X)
;;                                                ((QUOTE T)(FF (CAR X))))))

(deftest variable-tests
  (testing "Variable translation"
    (let [expected "X"
          actual (prin (generate (simplify (parse "x"))))]
      (is (= actual expected)))
    (let [expected "CAR"
          actual (prin (generate (simplify (parse "car"))))]
      (is (= actual expected)))
    ))

(deftest literal-tests
  (testing "Literal translation"
    ;; in the context of an M-expression, an upper case letter
    ;; or string represents a Lisp literal, and should be quoted.
    ;; Wrapping in a function call puts us into mexpr contest;
    ;; "T" would be interpreted as a sexpr, which would not be
    ;; quoted.
    (let [expected "(ATOM (QUOTE T))"
          actual (prin (generate (simplify (parse "atom[T]"))))]
      (is (= actual expected)))
    ;; I'm not clear how `car[(A B C)]` should be translated, but
    ;; I suspect as (CAR (LIST 'A 'B 'C)).
    ))

(deftest fncall-tests
  (testing "Function calls"
    (let [expected "(CAR X)"
          actual (prin (generate (simplify (parse "car[x]"))))]
      (is (= actual expected)))
    (let [expected "(FF (CAR X))"
          actual (prin (generate (simplify (parse "ff[car[x]]"))))]
      (is (= actual expected)))))

(deftest conditional-tests
  (testing "Conditional expressions"
    (let [expected "(COND ((ATOM X) X) ((QUOTE T) (FF (CAR X))))"
          actual (prin (generate (simplify (parse "[atom[x]->x; T->ff[car[x]]]"))))]
      (is (= actual expected)))
    (let [expected "(LABEL FF (LAMBDA (X) (COND ((ATOM X) X) ((QUOTE T) (FF (CAR X))))))"
          actual (prin
                   (generate
                     (simplify
                       (parse "label[ff;λ[[x];[atom[x]->x; T->ff[car[x]]]]]"))))]
      (is (= actual expected)))))

