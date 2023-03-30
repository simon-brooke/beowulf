(ns beowulf.lisp-test
  "The idea here is to test actual Lisp functions"
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [beowulf.bootstrap :refer [EVAL]]
            [beowulf.cons-cell :refer [make-beowulf-list]]
            [beowulf.io :refer [SYSIN]]
            [beowulf.read :refer [READ]]))

;; (use-fixtures :once (fn [f]
;;                        (try (SYSIN "resources/lisp1.5.lsp")
;;                        (f)
;;                             (catch Throwable any
;;                               (throw (ex-info "Failed to load Lisp sysout"
;;                                               {:phase test
;;                                                :function 'SYSIN
;;                                                :file "resources/lisp1.5.lsp"}))))))

;; (deftest "COPY test"
;;   ;; (testing "copy NIL"
;;   ;;   (println "in-test: " (SYSIN "resources/lisp1.5.lsp"))
;;   ;;   (let [expected "NIL"
;;   ;;         actual (with-out-str (println (EVAL (READ "(COPY NIL)"))))]
;;   ;;     (is (= actual expected))))
;;   (testing "copy straight list"
;;     (println "in-test: " (SYSIN "resources/lisp1.5.lsp"))
;;     (let [expected (make-beowulf-list '(A B C))
;;           actual (with-out-str (print (EVAL (READ "(COPY '(A B C))"))))]
;;       (is (= actual expected))))
;;   (testing "copy assoc list"
;;     (let [expected "((A . 1) (B . 2) (C . 3))"
;;           actual (with-out-str (println (EVAL (READ "(COPY '((A . 1) (B . 2) (C . 3)))"))))]
;;       (is (= actual expected)))))
