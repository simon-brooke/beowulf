(ns beowulf.mexpr-test
  "These tests are taken generally from the examples on page 10 of
   Lisp 1.5 Programmers Manual"
  (:require [clojure.test :refer [deftest is testing]]
            [beowulf.bootstrap :refer [*options*]] 
            [beowulf.read :refer [gsp]]
            [beowulf.reader.generate :refer [generate]]
            [beowulf.reader.parser :refer [parse]]
            [beowulf.reader.simplify :refer [simplify]]))

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
          actual (print-str (gsp "x"))]
      (is (= actual expected)))
    (let [expected "CAR"
          actual (print-str (gsp "car"))]
      (is (= actual expected)))
    ))

(deftest literal-tests
  (testing "Literal translation"
    ;; in the context of an M-expression, an upper case letter
    ;; or string represents a Lisp literal, and should be quoted.
    ;; Wrapping in a function call puts us into mexpr contest;
    ;; "T" would be interpreted as a sexpr, which would not be
    ;; quoted.
    (let [expected "(ATOM A)"
          actual (print-str (gsp "atom[A]"))]
      (is (= actual expected)))
    ;; I'm not clear how `car[(A B C)]` should be translated, but
    ;; I suspect as (CAR (LIST A B C)).
    (let [expected "(CAR (LIST A B C))"
          actual (print-str (gsp "car[(A B C)]"))]
      (is (= actual expected)))
    ))

(deftest fncall-tests
  (testing "Function calls"
    (let [expected "(CAR X)"
          actual (print-str (gsp "car[x]"))]
      (is (= actual expected)))
    (let [expected "(FF (CAR X))"
          actual (print-str (gsp "ff[car[x]]"))]
      (is (= actual expected)))))

(deftest conditional-tests
  (testing "Conditional expressions"
    (let [expected "(COND ((ATOM X) X) (T (FF (CAR X))))"
          actual (print-str (gsp "[atom[x]->x; T->ff[car[x]]]"))]
      (is (= actual expected)))
    (let [expected "(LABEL FF (LAMBDA (X) (COND ((ATOM X) X) (T (FF (CAR X))))))"
          actual (print-str
                   (generate
                     (simplify
                       (parse "label[ff;λ[[x];[atom[x]->x; T->ff[car[x]]]]]"))))]
      (is (= actual expected)))))

(deftest strict-tests
  (testing "Strict feature"
    (binding [*options* {:strict true}]
      (is (thrown-with-msg?
            Exception
            #"Cannot parse meta expressions in strict mode"
            (gsp "label[ff;λ[[x];[atom[x]->x; T->ff[car[x]]]]]"))))))

(deftest assignment-tests
  (testing "Function assignment"
    (let [expected "(SET (QUOTE FF) (QUOTE (LAMBDA (X) (COND ((ATOM X) X) (T (FF (CAR X)))))))"
          actual (print-str (gsp "ff[x]=[atom[x] -> x; T -> ff[car[x]]]"))]
      (is (= actual expected)))))
