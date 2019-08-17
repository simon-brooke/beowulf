(ns beowulf.bootstrap-test
  (:require [clojure.math.numeric-tower :refer [abs]]
            [clojure.test :refer :all]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell NIL T F]]
            [beowulf.eval :refer :all]
            [beowulf.read :refer [gsp]]))

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
          actual (primitive-atom (gsp "HELLO"))]
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
          actual (primitive-atom (gsp "(A B C D)"))]
      (is (= actual expected) "A list is explicitly not an atom")))
  (testing "primitive-atom?"
    (let [expected T
          actual (primitive-atom? T)]
      (is (= actual expected) "T is an atom (symbol)"))
    (let [expected T
          actual (primitive-atom? (gsp "HELLO"))]
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
          actual (primitive-atom? (gsp "(A B C D)"))]
      (is (= actual expected) "A list is explicitly not an atom"))))

(deftest access-function-tests
  (testing "car"
    (let [expected 'A
          actual (car (make-cons-cell 'A 'B))]
      (is (= actual expected) "A is car of (A . B)"))
    (let [expected 'A
          actual (car (gsp "(A B C D)"))]
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
          actual (cdr (gsp "(A B C D)"))]
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
  (let [s (gsp "((((1 . 2) 3)(4 5) 6)(7 (8 9) (10 11 12) 13) 14 (15 16) 17)")]
    ;; structure for testing access functions
    (testing "cadr"
      (let [expected 'B
            actual (cadr (gsp "(A B C D)"))]
        (is (= actual expected))))
    (testing "caddr"
      (let [expected 'C
            actual (caddr (gsp "(A B C D)"))]
        (is (= actual expected)))
      (let [expected 14
            actual (caddr s)]
        (is (= actual expected)))
      )
    (testing "cadddr"
      (let [expected 'D
            actual (cadddr (gsp "(A B C D)"))]
        (is (= actual expected))))
    (testing "caaaar"
      (let [expected "1"
            actual (print-str (caaaar s))]
        (is (= actual expected))))))


(deftest equality-tests
  (testing "eq"
    (let [expected 'T
          actual (eq 'FRED 'FRED)]
      (is (= actual expected) "identical symbols"))
    (let [expected 'F
          actual (eq 'FRED 'ELFREDA)]
      (is (= actual expected) "different symbols"))
    (let [expected 'F
          l (gsp "(NOT AN ATOM)")
          actual (eq l l)]
      (is (= actual expected) "identical lists (eq is not defined for lists)")))
  (testing "equal"
    (let [expected 'T
          actual (equal 'FRED 'FRED)]
      (is (= actual expected) "identical symbols"))
    (let [expected 'F
          actual (equal 'FRED 'ELFREDA)]
      (is (= actual expected) "different symbols"))
    (let [expected 'T
          l (gsp "(NOT AN ATOM)")
          actual (equal l l)]
      (is (= actual expected) "same list, same content"))
    (let [expected 'T
          l (gsp "(NOT AN ATOM)")
          m (gsp "(NOT AN ATOM)")
          actual (equal l m)]
      (is (= actual expected) "different lists, same content"))
    (let [expected 'F
          l (gsp "(NOT AN ATOM)")
          m (gsp "(NOT REALLY AN ATOM)")
          actual (equal l m)]
      (is (= actual expected) "different lists, different content"))))

(deftest substitution-tests
  (testing "subst"
    (let [expected "((A X . A) . C)"
          ;; differs from example in book only because of how the function
          ;; `beowulf.cons-cell/to-string` formats lists.
          actual (print-str
                   (subst
                     (gsp "(X . A)")
                     (gsp "B")
                     (gsp "((A . B) . C)")))]
      (is (= actual expected)))))

(deftest append-tests
  (testing "append"
    (let [expected "(A B C . D)"
          actual (print-str
                   (append
                     (gsp "(A B)")
                     (gsp "(C . D)")))]
      (is (= actual expected)))
    (let [expected "(A B C D E)"
          actual (print-str
                   (append
                     (gsp "(A B)")
                     (gsp "(C D E)")))]
      (is (= actual expected)))))

(deftest member-tests
  (testing "member"
    (let [expected 'T
          actual (member (gsp "ALBERT") (gsp "(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED)"))]
      (= actual expected))
    (let [expected 'T
          actual (member (gsp "BELINDA") (gsp "(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED)"))]
      (= actual expected))
    (let [expected 'T
          actual (member (gsp "ELFREDA") (gsp "(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED)"))]
      (= actual expected))
    (let [expected 'F
          actual (member (gsp "BERTRAM") (gsp "(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED)"))]
      (= actual expected))))

(deftest pairlis-tests
  (testing "pairlis"
    (let [expected "((A . U) (B . V) (C . W) (D . X) (E . Y))"
          actual (print-str
                   (pairlis
                     (gsp "(A B C)")
                     (gsp "(U V W)")
                     (gsp "((D . X)(E . Y))")))]
      (is (= actual expected)))))

(deftest assoc-tests
  (testing "assoc"
    (let [expected "(B CAR X)"
          actual (print-str
                   (primitive-assoc
                     'B
                     (gsp "((A . (M N)) (B . (CAR X)) (C . (QUOTE M)) (C . (CDR X)))")))]
      (is (= actual expected)))
    (let [expected "(C QUOTE M)"
          actual (print-str
                   (primitive-assoc
                     'C
                     (gsp "((A . (M N)) (B . (CAR X)) (C . (QUOTE M)) (C . (CDR X)))")))]
      (is (= actual expected)))
    (let [expected "NIL"
          actual (print-str
                   (primitive-assoc
                     'D
                     (gsp "((A . (M N)) (B . (CAR X)) (C . (QUOTE M)) (C . (CDR X)))")))]
      (is (= actual expected)))))

(deftest sublis-tests
  (testing "sublis"
    (let [expected "(SHAKESPEARE WROTE (THE TEMPEST))"
          actual (print-str
                   (sublis
                     (gsp "((X . SHAKESPEARE) (Y . (THE TEMPEST)))")
                     (gsp "(X WROTE Y)")))]
      (is (= actual expected)))))
