(ns beowulf.bootstrap-test
  (:require [clojure.math.numeric-tower :refer [abs]]
            [clojure.test :refer :all]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL T F]]
            [beowulf.bootstrap :refer :all]
            [beowulf.read :refer [gsp]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; This file is primarily tests of the functions in `beowulf.eval` - which
;;; are Clojure functions, but aim to provide sufficient functionality that
;;; Beowulf can get up to the level of running its own code.
;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest atom-tests
  (testing "ATOM"
    (let [expected T
          actual (ATOM T)]
      (is (= actual expected) "T is an atom (symbol)"))
    (let [expected T
          actual (ATOM (gsp "HELLO"))]
      (is (= actual expected) "HELLO is an atom (symbol)"))
    (let [expected T
          actual (ATOM 7)]
      (is (= actual expected)
          "I'm not actually certain whether a number should be treated as an
          atom, but I'm guessing so"))
    (let [expected F
          actual (ATOM (make-cons-cell 'A 'B))]
      (is (= actual expected) "A dotted pair is explicitly not an atom."))
    (let [expected F
          actual (ATOM (gsp "(A B C D)"))]
      (is (= actual expected) "A list is explicitly not an atom")))
  (testing "ATOM?"
    (let [expected T
          actual (ATOM? T)]
      (is (= actual expected) "T is an atom (symbol)"))
    (let [expected T
          actual (ATOM? (gsp "HELLO"))]
      (is (= actual expected) "HELLO is an atom (symbol)"))
    (let [expected T
          actual (ATOM? 7)]
      (is (= actual expected)
          "I'm not actually certain whether a number should be treated as an
          atom, but I'm guessing so"))
    (let [expected NIL
          actual (ATOM? (make-cons-cell 'A 'B))]
      (is (= actual expected) "A dotted pair is explicitly not an atom."))
    (let [expected NIL
          actual (ATOM? (gsp "(A B C D)"))]
      (is (= actual expected) "A list is explicitly not an atom"))))

(deftest access-function-tests
  (testing "CAR"
    (let [expected 'A
          actual (CAR (make-cons-cell 'A 'B))]
      (is (= actual expected) "A is CAR of (A . B)"))
    (let [expected 'A
          actual (CAR (gsp "(A B C D)"))]
      (is (= actual expected) "A is CAR of (A B C D)"))
    (is (thrown-with-msg?
          Exception
          #"Cannot take CAR of `.*"
          (CAR 'T))
        "Can't take the CAR of an atom")
    (is (thrown-with-msg?
          Exception
          #"Cannot take CAR of `.*"
          (CAR 7))
        "Can't take the CAR of a number"))
  (testing "CDR"
    (let [expected 'B
          actual (CDR (make-cons-cell 'A 'B))]
      (is (= actual expected) "B is CDR of (A . B)"))
    (let [expected 'B
          actual (CDR (gsp "(A B C D)"))]
      (is (instance? beowulf.substrate.ConsCell actual)
          "CDR of (A B C D) is a cons cell")
      (is (= (CAR actual) expected) "the CAR of that cons-cell is B"))
    (is (thrown-with-msg?
          Exception
          #"Cannot take CDR of `.*"
          (CDR 'T))
        "Can't take the CDR of an atom")
    (is (thrown-with-msg?
          Exception
          #"Cannot take CDR of `.*"
          (CDR 7))
        "Can't take the CDR of a number"))
  (let [s (gsp "((((1 . 2) 3)(4 5) 6)(7 (8 9) (10 11 12) 13) 14 (15 16) 17)")]
    ;; structure for testing access functions
    (testing "cadr"
      (let [expected 'B
            actual (CADR (gsp "(A B C D)"))]
        (is (= actual expected))))
    (testing "caddr"
      (let [expected 'C
            actual (CADDR (gsp "(A B C D)"))]
        (is (= actual expected)))
      (let [expected 14
            actual (CADDR s)]
        (is (= actual expected)))
      )
    (testing "cadddr"
      (let [expected 'D
            actual (CADDDR (gsp "(A B C D)"))]
        (is (= actual expected))))
    (testing "caaaar"
      (let [expected "1"
            actual (print-str (CAAAAR s))]
        (is (= actual expected))))))


(deftest equality-tests
  (testing "eq"
    (let [expected 'T
          actual (EQ 'FRED 'FRED)]
      (is (= actual expected) "identical symbols"))
    (let [expected 'F
          actual (EQ 'FRED 'ELFREDA)]
      (is (= actual expected) "different symbols"))
    (let [expected 'F
          l (gsp "(NOT AN ATOM)")
          actual (EQ l l)]
      (is (= actual expected) "identical lists (EQ is not defined for lists)")))
  (testing "equal"
    (let [expected 'T
          actual (EQUAL 'FRED 'FRED)]
      (is (= actual expected) "identical symbols"))
    (let [expected 'F
          actual (EQUAL 'FRED 'ELFREDA)]
      (is (= actual expected) "different symbols"))
    (let [expected 'T
          l (gsp "(NOT AN ATOM)")
          actual (EQUAL l l)]
      (is (= actual expected) "same list, same content"))
    (let [expected 'T
          l (gsp "(NOT AN ATOM)")
          m (gsp "(NOT AN ATOM)")
          actual (EQUAL l m)]
      (is (= actual expected) "different lists, same content"))
    (let [expected 'F
          l (gsp "(NOT AN ATOM)")
          m (gsp "(NOT REALLY AN ATOM)")
          actual (EQUAL l m)]
      (is (= actual expected) "different lists, different content"))))

(deftest substitution-tests
  (testing "subst"
    (let [expected "((A X . A) . C)"
          ;; differs from example in book only because of how the function
          ;; `beowulf.cons-cell/to-string` formats lists.
          actual (print-str
                   (SUBST
                     (gsp "(X . A)")
                     (gsp "B")
                     (gsp "((A . B) . C)")))]
      (is (= actual expected)))))

(deftest append-tests
  (testing "append"
    (let [expected "(A B C . D)"
          actual (print-str
                   (APPEND
                     (gsp "(A B)")
                     (gsp "(C . D)")))]
      (is (= actual expected)))
    (let [expected "(A B C D E)"
          actual (print-str
                   (APPEND
                     (gsp "(A B)")
                     (gsp "(C D E)")))]
      (is (= actual expected)))))

(deftest member-tests
  (testing "member"
    (let [expected 'T
          actual (MEMBER
                   (gsp "ALBERT")
                   (gsp "(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED)"))]
      (= actual expected))
    (let [expected 'T
          actual (MEMBER
                   (gsp "BELINDA")
                   (gsp "(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED)"))]
      (= actual expected))
    (let [expected 'T
          actual (MEMBER
                   (gsp "ELFREDA")
                   (gsp "(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED)"))]
      (= actual expected))
    (let [expected 'F
          actual (MEMBER
                   (gsp "BERTRAM")
                   (gsp "(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED)"))]
      (= actual expected))))

(deftest pairlis-tests
  (testing "pairlis"
    (let [expected "((A . U) (B . V) (C . W) (D . X) (E . Y))"
          actual (print-str
                   (PAIRLIS
                     (gsp "(A B C)")
                     (gsp "(U V W)")
                     (gsp "((D . X)(E . Y))")))]
      (is (= actual expected)))))

(deftest assoc-tests
  (testing "assoc"
    (let [expected "(B CAR X)"
          actual (print-str
                   (ASSOC
                     'B
                     (gsp "((A . (M N)) (B . (CAR X)) (C . (QUOTE M)) (C . (CDR X)))")))]
      (is (= actual expected)))
    (let [expected "(C QUOTE M)"
          actual (print-str
                   (ASSOC
                     'C
                     (gsp "((A . (M N)) (B . (CAR X)) (C . (QUOTE M)) (C . (CDR X)))")))]
      (is (= actual expected)))
    (let [expected "NIL"
          actual (print-str
                   (ASSOC
                     'D
                     (gsp "((A . (M N)) (B . (CAR X)) (C . (QUOTE M)) (C . (CDR X)))")))]
      (is (= actual expected)))))

(deftest sublis-tests
  (testing "sublis"
    (let [expected "(SHAKESPEARE WROTE (THE TEMPEST))"
          actual (print-str
                   (SUBLIS
                     (gsp "((X . SHAKESPEARE) (Y . (THE TEMPEST)))")
                     (gsp "(X WROTE Y)")))]
      (is (= actual expected)))))
