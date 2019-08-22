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

