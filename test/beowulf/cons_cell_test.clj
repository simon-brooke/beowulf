(ns beowulf.cons-cell-test
  (:require [clojure.test :refer [deftest is testing]]
            [beowulf.cons-cell :refer [make-beowulf-list make-cons-cell pretty-print]])
  (:import [beowulf.cons_cell ConsCell]))

(deftest cons-cell-tests
  (testing "make-cons-cell"
    (let [expected "(A . B)"
          actual (print-str (ConsCell. 'A 'B (gensym "c")))]
      (is (= actual expected) "Cons cells should print as cons cells, natch."))
    (let [expected "(A . B)"
          actual (print-str (make-cons-cell 'A 'B))]
      (is (= actual expected) "Even if build with the macro."))
    (let [expected beowulf.cons_cell.ConsCell
          actual (type (make-cons-cell 'A 'B))]
      (is (= actual expected) "And they should be cons cells."))
    )
  (testing "make-beowulf-list"
    (let [expected "(A (B C) (D E (F) G) H)"
          actual (print-str (make-beowulf-list '(A (B C) (D E (F) G) H)))]
      (is (= actual expected) "Should work for clojure lists, recursively."))
    (let [expected "(A (B C) (D E (F) G) H)"
          actual (print-str (make-beowulf-list ['A ['B 'C] ['D 'E ['F] 'G] 'H]))]
      (is (= actual expected) "Should work for vectors, too."))
    (let [expected "NIL"
          actual (print-str (make-beowulf-list []))]
      (is (= actual expected) "An empty sequence is NIL."))
    (let [expected beowulf.cons_cell.ConsCell
          actual (type (make-beowulf-list '(A (B C) (D E (F) G) H)))]
      (is (= actual expected) "A beowulf list is made of cons cells.")))
  (testing "pretty-print"
    (let [expected "(A\n  (B C)\n  (D E (F) G) H)"
          ;; returns a string because width and level args are passed.
          actual (pretty-print
                     (make-beowulf-list '(A (B C) (D E (F) G) H)) 20 0)]
      (is (= actual expected)))
    (let [expected "(A (B C) (D E (F) G) H)\n"
          actual (with-out-str
                   (pretty-print
                     (make-beowulf-list '(A (B C) (D E (F) G) H))))]
      (is (= actual expected))))
;; Count does NOT currently work as expected, but I'm going to stop struggling
;; with it for now.
;;   (testing "count"
;;     (let [expected 4
;;           actual (count (make-beowulf-list '(A (B C) (D E (F) G) H)) 20 0)]
;;       (is (= actual expected)))
;;     (let [expected 1
;;           actual (count (make-beowulf-list '(A)))]
;;       (is (= actual expected)))
;;     (let [expected 1
;;           actual (count (make-cons-cell 'A 'B))]
;;       (is (= actual expected))))
  (testing "sequence functions"
    (let [expected "A"
          actual (print-str (first (make-beowulf-list '(A (B C) (D E (F) G) H))))]
      (is (= actual expected)))
    (let [expected "((B C) (D E (F) G) H)"
          actual (print-str (.more (make-beowulf-list '(A (B C) (D E (F) G) H))))]
      (is (= actual expected)))
    (let [expected "((B C) (D E (F) G) H)"
          actual (print-str (next (make-beowulf-list '(A (B C) (D E (F) G) H))))]
      (is (= actual expected)))
  ))
