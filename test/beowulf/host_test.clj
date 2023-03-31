(ns beowulf.host-test
  (:require [clojure.test :refer [deftest is testing]]
            [beowulf.cons-cell :refer [F make-beowulf-list T]] 
            [beowulf.host :refer [CDR DIFFERENCE NUMBERP PLUS RPLACA RPLACD TIMES]]
            [beowulf.oblist :refer [NIL]]
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

(deftest numberp-tests
  (testing "NUMBERP"
    (let [expected T
          actual   (NUMBERP 7)]
      (is (= actual expected) "7 is a number"))
    (let [expected T
          actual   (NUMBERP 3.14)]
      (is (= actual expected) "3.14 is a number"))
    (let [expected F
          actual   (NUMBERP NIL)]
      (is (= actual expected) "NIL is not a number"))
    (let [expected F
          actual   (NUMBERP (gsp "HELLO"))]
      (is (= actual expected) "HELLO is not a number"))))

(deftest arithmetic-test
  ;; These are just sanity-test tests; they're by no means exhaustive.
  (testing "PLUS"
    (let [expected 3
          actual (PLUS 1 2)]
      (is (= actual expected))
      (is (integer? actual)))
    (let [expected 3.5
          actual (PLUS 1.25 9/4)]
      (is (= actual expected))
      (is (float? actual))))
  (testing "TIMES"
    (let [expected 6
          actual (TIMES 2 3)]
      (is (= actual expected))))
  (testing DIFFERENCE
    (let [expected -1
          actual (DIFFERENCE 1 2)]
      (is (= actual expected)))))
