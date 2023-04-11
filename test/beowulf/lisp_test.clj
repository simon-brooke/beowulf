(ns beowulf.lisp-test
  "The idea here is to test actual Lisp functions"
  (:require [beowulf.bootstrap :refer [EVAL]]
            [beowulf.cons-cell :refer [make-beowulf-list]]
            [beowulf.io :refer [SYSIN]] ;; [beowulf.oblist :refer [NIL]]
            [beowulf.oblist :refer [NIL]]
            [beowulf.read :refer [READ]]
            [clojure.test :refer [deftest is testing use-fixtures]]))

(defn- reps
  "'Read eval print string', or 'read eval print single'.
   Reads and evaluates one input string, and returns the
   output string."
  [input]
  (with-out-str (print (EVAL (READ input)))))

(use-fixtures :once (fn [f]
                      (try (when (SYSIN "resources/lisp1.5.lsp")
                             (f))
                           (catch Throwable any
                             (throw (ex-info "Failed to load Lisp sysout"
                                             {:phase test
                                              :function 'SYSIN
                                              :file "resources/lisp1.5.lsp"}
                                             any))))))

(deftest APPEND-tests
  (testing "append - dot-terminated lists"
    (let [expected "(A B C . D)"
          actual (reps "(APPEND '(A B) (CONS 'C 'D))")]
      (is (= actual expected)))
    (let [expected "(A B C . D)"
          actual (reps "(APPEND (CONS 'A (CONS 'B NIL)) (CONS 'C 'D))")]
      (is (= actual expected)))
      ;; this is failing: https://github.com/simon-brooke/beowulf/issues/5
    (let [expected "(A B C . D)"
          actual (reps "(APPEND '(A B) '(C . D))")]
      (is (= actual expected))))
  (testing "append - straight lists"
    (let [expected "(A B C D E)"
          actual (reps "(APPEND '(A B) '(C D E))")]
      (is (= actual expected)))))

(deftest COPY-tests
  (testing "copy NIL"
    (let [expected "NIL"
          actual (with-out-str (print (EVAL (READ "(COPY NIL)"))))]
      (is (= actual expected))))
  (testing "copy straight list"
    (let [expected (make-beowulf-list '(A B C))
          actual (EVAL (READ "(COPY '(A B C))"))]
      (is (= actual expected))))
  (testing "copy assoc list created in READ"
    ;; this is failing. Problem in READ?
    ;; see https://github.com/simon-brooke/beowulf/issues/5
    (let [expected (READ "((A . 1) (B . 2) (C . 3))")
          actual (EVAL (READ "(COPY '((A . 1) (B . 2) (C . 3)))"))]
      (is (= actual expected))))
  (testing "copy assoc list created with PAIR"
    (let [expected (READ "((A . 1) (B . 2) (C . 3))")
          actual (EVAL (READ "(COPY (PAIR '(A B C) '(1 2 3)))"))]
      (is (= actual expected)))))

(deftest DIVIDE-tests
  (testing "rational divide"
    (let [expected "(4 0)"
          input "(DIVIDE 8 2)"
          actual (reps input)]
      (is (= actual expected))))
  (testing "irrational divide"
    (let [expected "(3.142857 1)"
          input "(DIVIDE 22 7)"
          actual (reps input)]
      (is (= actual expected))))
  (testing "divide by zero"
    (let [input "(DIVIDE 22 0)"]
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Uncynlic þegnung: Divide by zero"
                            (reps input)))))

  ;; TODO: need to write tests for GET but I don't really
  ;; understand what the correct behaviour is.

  (deftest INTERSECTION-tests
    (testing "non-intersecting"
      (let [expected "NIL"
            input "(INTERSECTION '(A B C) '(D E F))"
            actual (reps input)]
        (is (= actual expected))))
    (testing "intersection with NIL"
      (let [expected "NIL"
            input "(INTERSECTION '(A B C) NIL)"
            actual (reps input)]
        (is (= actual expected))))
    (testing "intersection with NIL (2)"
      (let [expected "NIL"
            input "(INTERSECTION NIL '(A B C))"
            actual (reps input)]
        (is (= actual expected))))
    (testing "sequential intersection"
      (let [expected "(C D)"
            input "(INTERSECTION '(A B C D) '(C D E F))"
            actual (reps input)]
        (is (= actual expected))))
    (testing "non-sequential intersection"
      (let [expected "(C D)"
            input "(INTERSECTION '(A B C D) '(F D E C))"
            actual (reps input)]
        (is (= actual expected)))))

  (deftest LENGTH-tests
    (testing "length of NIL"
      (let [expected "0"
            input "(LENGTH NIL)"
            actual (reps input)]
        (is (= actual expected))))
    (testing "length of simple list"
      (let [expected "3"
            input "(LENGTH '(1 2 3))"
            actual (reps input)]
        (is (= actual expected))))
    ;; (testing "length of dot-terminated list"
    ;;   (let [expected "3"
    ;;         input "(LENGTH '(1 2 3 . 4))"
    ;;         actual (reps input)]
    ;;     (is (= actual expected))))
    (testing "length of assoc list"
      (let [expected "3"
            input "(LENGTH (PAIR '(A B C) '(1 2 3)))"
            actual (reps input)]
        (is (= actual expected))))))


(deftest MEMBER-tests
  (testing "member"
    (let [expected "T"
          actual (reps "(MEMBER 'ALBERT '(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED))")]
      (is (= actual expected)))
    (let [expected "T"
          actual (reps "(MEMBER 'BELINDA '(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED))")]
      (is (= actual expected)))
    (let [expected "T"
          actual (reps "(MEMBER 'ELFREDA '(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED))")]
      (is (= actual expected)))
    (let [expected "F"
          actual (reps "(MEMBER 'BERTRAM '(ALBERT BELINDA CHARLIE DORIS ELFREDA FRED))")]
      (is (= actual expected)))))

;; This is failing, and although yes, it does matter, I have not yet tracked the reason.
;; (deftest sublis-tests
;;   (testing "sublis"
;;     (let [expected "(SHAKESPEARE WROTE (THE TEMPEST))"
;;           actual (reps
;;                    "(SUBLIS
;;                      '((X . SHAKESPEARE) (Y . (THE TEMPEST)))
;;                      '(X WROTE Y))")]
;;       (is (= actual expected)))))

(deftest prog-tests
  (testing "PROG"
    (let [expected "5"
          actual (reps "(PROG (X)
    (SETQ X 1)
    START
    (SETQ X (ADD1 X))
    (COND ((EQ X 5) (RETURN X))
        (T (GO START))))")]
      (is (= actual expected)))))


(deftest put-get-tests
  (let [symbol 'TESTSYMBOL
        p1 'TESTPROPONE
        p2 'TESTPROPTWO]
    (testing "GET - property should be missing"
      (let [expected "NIL"
            actual (reps "(GET 'TESTSYMBOL 'TESTPROPONE)")]
        (is (= actual expected))))
    (testing "PUT and GET: value of new property; change value of property"
      (let [prop (reps "(GENSYM)")
            val1 (reps "(GENSYM)")
            val2 (reps "(GENSYM)")
            expected1 val1
            actual1 (when (reps (str "(PUT '" symbol " '" prop " '" val1 ")"))
                      (reps (str "(GET '" symbol " '" prop ")")))
            expected2 val2
            actual2 (when (reps (str "(PUT '" symbol " '" prop " '" val2 ")"))
                      (reps (str "(GET '" symbol " '" prop ")")))]
        (is (not= val1 val2))
        (is (= actual1 expected1) "The value set can be retrieved.")
        (is (= actual2 expected2) "The value is changed.")))
    (testing "PUT and GET: different properties have independent values"
      (let [val1 (reps "(GENSYM)")
            val2 (reps "(GENSYM)")
            expected1 val1
            actual1 (when (reps (str "(PUT '" symbol " '" p1 " '" val1 ")"))
                      (reps (str "(GET '" symbol " '" p1 ")")))
            expected2 val2
            actual2 (when (reps (str "(PUT '" symbol " '" p2 " '" val2 ")"))
                      (reps (str "(GET '" symbol " '" p2 ")")))
            expected3 val1
            actual3 (reps (str "(GET '" symbol " '" p1 ")"))]
        (is (not= val1 val2))
        (is (= actual1 expected1) "The value set can be retrieved.")
        (is (= actual2 expected2) "Values are independent.")
        (is (= actual3 expected3) "Setting a second property does not obliterate the first.")))))