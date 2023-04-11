(ns beowulf.host-test
  (:require [beowulf.cons-cell :refer [F make-beowulf-list make-cons-cell T]]
            [beowulf.host :refer [ADD1 AND CADDDR CAR CDR DEFINE DIFFERENCE
                                  ERROR FIXP GREATERP lax? LESSP NILP NULL
                                  NUMBERP OR PLUS RPLACA RPLACD SUB1 TIMES uaf]]
            [beowulf.io :refer [SYSIN]]
            [beowulf.oblist :refer [*options* NIL]]
            [beowulf.read :refer [gsp]]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [expectations.clojure.test
             :refer [defexpect expect more-> more-of]]))

(use-fixtures :once (fn [f]
                      (try (when (SYSIN "resources/lisp1.5.lsp")
                             (f))
                           (catch Throwable any
                             (throw (ex-info "Failed to load Lisp sysout"
                                             {:phase test
                                              :function 'SYSIN
                                              :file "resources/lisp1.5.lsp"}
                                             any))))))

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
         #"Un-ġefōg þing in RPLACA.*"
         (RPLACA (make-beowulf-list '(A B C D E)) "F"))
        "You can't represent a string in Lisp 1.5")
    (is (thrown-with-msg?
         Exception
         #"Uncynlic miercels in RPLACA.*"
         (RPLACA '(A B C D E) 'F))
        "You can't RPLACA into anything which isn't a MutableSequence."))
  (testing "RPLACD"
    (let
     [l (make-beowulf-list '(A B C D E))
      target (CDR l)
      expected "(A B . F)"
      actual (do (RPLACD target 'F) (print-str l))]
      (is (= actual expected)))
    (let
     [l (make-beowulf-list '(A B C D E))
      target (CDR l)
      expected "(A B)"
      actual (do (RPLACD target NIL) (print-str l))]
      (is (= actual expected)))
    (is (thrown-with-msg?
         Exception
         #"Un-ġefōg þing in RPLACD.*"
         (RPLACD (make-beowulf-list '(A B C D E)) :a))
        "You can't represent a keyword in Lisp 1.5")
    (is (thrown-with-msg?
         Exception
         #"Uncynlic miercels in RPLACD.*"
         (RPLACD "ABCDE" 'F))
        "You can't RPLACD into anything which isn't a MutableSequence.")))

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
      (is (float? actual)))
    (let [expected 3.5
          actual (PLUS -2.5 6)]
      (is (= actual expected) "Negative numbers are cool.")
      (is (float? actual))))
  (testing "TIMES"
    (let [expected 6
          actual (TIMES 2 3)]
      (is (= actual expected)))
    (let [expected 2.5
          actual (TIMES 5 0.5)]
      (is (= actual expected))))
  (testing "DIFFERENCE"
    (let [expected -1
          actual (DIFFERENCE 1 2)]
      (is (= actual expected)))
    (let [expected (float 0.1)
          actual (DIFFERENCE -0.1 -0.2)]
      (is (= actual expected))))
  (testing "ADD1"
    (let [expected -1
          actual (ADD1 -2)]
      (is (= actual expected)))
    (let [expected (float 3.5)
          actual (ADD1 2.5)]
      (is (= actual expected))))
  (testing "SUB1"
    (let [expected -3
          actual (SUB1 -2)]
      (is (= actual expected)))
    (let [expected (float 1.5)
          actual (SUB1 2.5)]
      (is (= actual expected)))))

(deftest laxness
  (testing "lax"
    (let [expected true
          actual (lax? 'Test)]
      (is (= actual expected) "Pass, the Queen's Cat, and all's well")))
  (testing "strict"
    (binding [*options* (assoc *options* :strict true)]
      (is (thrown-with-msg? Exception #".*ne āfand innan Lisp 1.5" (lax? 'Test))))))

(deftest access-tests
  (testing "primitive access"
    (let [cell (make-cons-cell 1 7)]
      (let [expected 1
            actual (CAR cell)]
        (is (= actual expected)))
      (let [expected 7
            actual (CDR cell)]
        (is (= actual expected))))
    (is (thrown-with-msg? Exception #".*Ne can tace CAR of.*" (CAR 7)))
    (is (thrown-with-msg? Exception #".*Ne can tace CDR of.*" (CDR 'A)))
    (is (thrown-with-msg? Exception #".*Ne liste.*" (CADDDR "Foo")))
    (is (thrown-with-msg? Exception #".*uaf: unexpected letter in path.*"
                          (uaf (make-beowulf-list '(A B C D))
                               '(\d \a \z \e \d))))))

(deftest misc-predicate-tests
  (testing "NULL"
    (let [expected T
          actual (NULL NIL)]
      (is (= actual expected)))
    (let [expected F
          actual (NULL (make-beowulf-list '(A B C)))]
      (is (= actual expected))))
  (testing "NILP"
    (let [expected T
          actual (NILP NIL)]
      (is (= actual expected)))
    (let [expected NIL
          actual (NILP (make-beowulf-list '(A B C)))]
      (is (= actual expected))))
  (testing "AND"
    (let [expected T
          actual (AND)]
      (is (= actual expected)))
    (let [expected T
          actual (AND T T)]
      (is (= actual expected)))
    (let [expected T
          actual (AND T T T)]
      (is (= actual expected)))
    (let [expected T
          actual (AND 1 'A (make-beowulf-list '(A B C)))]
      (is (= actual expected)))
    (let [expected F
          actual (AND NIL)]
      (is (= actual expected)))
    (let [expected F
          actual (AND T T F T)]
      (is (= actual expected))))
  (testing "OR"
    (let [expected F
          actual (OR)]
      (is (= actual expected)))
    (let [expected T
          actual (OR NIL T)]
      (is (= actual expected)))
    (let [expected T
          actual (OR T F T)]
      (is (= actual expected)))
    (let [expected T
          actual (OR 1 F (make-beowulf-list '(A B C)))]
      (is (= actual expected)))
    (let [expected F
          actual (OR NIL)]
      (is (= actual expected)))
    (let [expected F
          actual (OR NIL F)]
      (is (= actual expected))))
  (testing "FIXP"
    (let [expected F
          actual (FIXP NIL)]
      (is (= actual expected)))
    (let [expected F
          actual (FIXP 'A)]
      (is (= actual expected)))
    (let [expected F
          actual (FIXP 3.2)]
      (is (= actual expected)))
    (let [expected T
          actual (FIXP 7)]
      (is (= actual expected)))) 
  (testing "LESSP"
    (let [expected F
          actual (LESSP 7 3)]
      (is (= actual expected)))
    (let [expected T
          actual (LESSP -7 3.5)]
      (is (= actual expected)))
    (let [expected F
          actual (LESSP 3.14 3.14)]
      (is (= actual expected))))
  (testing "GREATERP"
    (let [expected T
          actual (GREATERP 7 3)]
      (is (= actual expected)))
    (let [expected F
          actual (GREATERP -7 3.5)]
      (is (= actual expected)))
    (let [expected F
          actual (GREATERP 3.14 3.14)]
      (is (= actual expected)))))

;; Really tricky to get DEFINE set up for testing here. It works OK in the
;; REPL, but there's nonsense going on with lazy sequences. Better to 
;; reimplement in Lisp.
;; (deftest define-tests
;;   (testing "DEFINE"
;;     (let [expected "(FF)"
;;          actual (str (doall (DEFINE 
;;                       (gsp "((FF LAMBDA (X) (COND ((ATOM X) X) (T (FF (CAR X))))))"))))]
;;     (is (= actual expected)))))

(defexpect error-without-code
  (expect (more-> clojure.lang.ExceptionInfo type
                  (more-of {:keys [:phase :function :args :type :code]}
                           'A1 code) ex-data)
          (ERROR)))

(defexpect error-with-code
  (let [x 'X1]
    (expect (more-> clojure.lang.ExceptionInfo type
                  (more-of {:keys [:phase :function :args :type :code]}
                           x code) ex-data)
          (ERROR x))))
