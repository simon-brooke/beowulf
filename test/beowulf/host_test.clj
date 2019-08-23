(ns beowulf.host-test
  (:require [clojure.math.numeric-tower :refer [abs]]
            [clojure.test :refer :all]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL T F]]
            [beowulf.bootstrap :refer [CDR]]
            [beowulf.host :refer :all]
            [beowulf.read :refer [gsp]]))

(deftest destructive-change-test
  (testing "RPLACA"
    (let
      [l (make-beowulf-list '(A B C D E))
       target (CDR l)
       expected "(A F C D E)"
       actual (do (RPLACA target 'F) (print-str l))]
      (is (= actual expected)))
    (is (thrown-with-msg?
          Exception
          #"Invalid value in RPLACA.*"
          (RPLACA (make-beowulf-list '(A B C D E)) "F"))
        "You can't represent a string in Lisp 1.5")
    (is (thrown-with-msg?
          Exception
          #"Invalid cell in RPLACA.*"
          (RPLACA '(A B C D E) 'F))
        "You can't RPLACA into anything which isn't a MutableSequence.")
    )
  (testing "RPLACA"
    (let
      [l (make-beowulf-list '(A B C D E))
       target (CDR l)
       expected "(A B . F)"
       actual (do (RPLACD target 'F) (print-str l))]
      (is (= actual expected)))
    )
  )

(deftest arithmetic-test
  ;; These are just sanity-test tests; they're by no means exhaustive.
  (testing "PLUS2"
    (let [expected 3
          actual (PLUS2 1 2)]
      (is (= actual expected))
      (is (integer? actual)))
    (let [expected 3.5
          actual (PLUS2 1.25 9/4)]
      (is (= actual expected))
      (is (float? actual))))
  (testing "TIMES2"
    (let [expected 6
          actual (TIMES2 2 3)]
      (is (= actual expected))))
  (testing DIFFERENCE
    (let [expected -1
          actual (DIFFERENCE 1 2)]
      (is (= actual expected)))))
