(ns beowulf.core-test
  (:require [beowulf.core :refer [-main repl stop-word]]
            [beowulf.oblist :refer [*options*]]
            [clojure.java.io :refer [reader]]
            [clojure.string :refer [split starts-with?]]
            [clojure.test :refer [deftest is testing]]))

;; (deftest a-test
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

(defn string->stream
  "Copied shamelessly from
  https://stackoverflow.com/questions/38283891/how-to-wrap-a-string-in-an-input-stream"
  ([s] (string->stream s "UTF-8"))
  ([s encoding]
   (-> s
       (.getBytes encoding)
       (java.io.ByteArrayInputStream.))))

(deftest repl-tests
  (testing "quit functionality"
    (with-open [r (reader (string->stream stop-word))]
      (binding [clojure.core/*in* r
                *options* (assoc *options* :testing true)]
        (is (thrown-with-msg? Exception #"\nFærwell!" (repl "")))))
    (let [expected nil
          actual (with-open [r (reader (string->stream stop-word))]
                   (binding [*in* r]
                     (-main "--testing")))]
      (is (= actual expected)))))

;; The new read-chars interface is really messing with this. Need to sort out!
;; OK, binding `:testing` doesn't work because `*options*` gets rebound in main.
;; Need to be able to pass in a testing flag as argument to -main
(deftest flag-tests
  (testing "Only testing flag"
    (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
          expected-quit-message (str "Sprecan '" stop-word "' tó laéfan")
          expected-result #".*\(3 \. 4\)"
          expected-prompt "Sprecan:: "
          expected-signoff "Færwell!"
          ;; anticipated output (note blank lines):

          ;; Hider wilcuman. Béowulf is mín nama.

          ;; Sprecan 'STOP' tó laéfan

          ;; Sprecan:: >  (3 . 4)
          ;; Sprecan:: 
          ;; Færwell!
          [_ greeting _ _ quit-message _ result prompt signoff]
          (with-open [r (reader (string->stream (str "cons[3; 4]\n" stop-word)))]
            (binding [*in* r]
              (split (with-out-str (-main "--testing")) #"\n")))]
      (is (= greeting expected-greeting))
      (is (re-matches expected-result result))
      (is (= quit-message expected-quit-message))
      (is (= prompt expected-prompt))
      (is (= signoff expected-signoff))))
  (testing "unknown flag"
    (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
          expected-quit-message (str "Sprecan '" stop-word "' tó laéfan")
          expected-error #"Unknown option:.*"
          expected-result #".*\(5 \. 6\)"
          expected-prompt "Sprecan:: "
          expected-signoff "Færwell!"
          [_ greeting _ error quit-message _ result prompt signoff]
          (with-open [r (reader (string->stream (str "cons[5; 6]\n" stop-word)))]
            (binding [*in* r]
              (split (with-out-str (-main "--unknown" "--testing")) #"\n")))]
      (is (= greeting expected-greeting))
      (is (re-matches expected-error error))
      (is (re-matches expected-result result))
      (is (= quit-message expected-quit-message))
      (is (= prompt expected-prompt))
      (is (= signoff expected-signoff))))
;; ;; TODO: not working because STOP is not being recognised, but I haven't
;; ;; worked out why not yet. It *did* work.

;; Hider wilcuman. Béowulf is mín nama.
;;   -f FILEPATH, --file-path FILEPATH               Set the path to the directory for reading and writing Lisp files.
;;   -h, --help
;;   -p PROMPT, --prompt PROMPT         Sprecan::    Set the REPL prompt to PROMPT
;;   -r SYSOUTFILE, --read SYSOUTFILE   lisp1.5.lsp  Read Lisp system from file SYSOUTFILE
;;   -s, --strict                                    Strictly interpret the Lisp 1.5 language, without extensions.
;;   -t, --time                                      Time evaluations.
;;   -x, --testing                                   Disable the jline reader - useful when piping input.
;; Sprecan 'STOP' tó laéfan

;; Sprecan:: 

  (testing "help"
    (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
          expected-h1 "  -h, --help"
          expected-quit-message (str "Sprecan '" stop-word "' tó laéfan")
          expected-result #".*\(A \. B\)"
          expected-prompt "Sprecan:: "
          expected-signoff "Færwell!"
          [_ greeting _ _ h1 _ _ _ _ _ quit-message _ result prompt signoff]
          (with-open [r (reader (string->stream (str "cons[A; B]\n" stop-word)))]
            (binding [*in* r]
              (split (with-out-str (-main "--help" "--testing")) #"\n")))]
      (is (= greeting expected-greeting))
      (is (= h1 expected-h1))
      (is (re-matches expected-result result))
      (is (= quit-message expected-quit-message))
      (is (starts-with? prompt expected-prompt))
      (is (= signoff expected-signoff))))
  (testing "prompt"
    (let [expected-prompt "? "
          [_ _ _ _ _ _ prompt _]
          (with-open [r (reader (string->stream stop-word))]
            (binding [*in* r]
              (split (with-out-str (-main "--prompt" "?" "--testing")) #"\n")))]
      (is (= prompt expected-prompt))))
   (testing "read - file not found"
     (let [expected-error #"Failed to validate.*"
           [_ _ _ error _ _ _ _ _]
           (with-open [r (reader (string->stream (str "cons[A; B]\n" stop-word)))]
             (binding [*in* r]
               (split (with-out-str (-main "--testing" "--read" "froboz")) #"\n")))]
       (is (re-matches expected-error error))))
   (testing "strict"
     (let [expected-result #".*Cannot parse meta expressions in strict mode.*"
           [_ _ _ _ _ _ result _ _]
           (with-open [r (reader (string->stream (str "cons[A; B]\n" stop-word)))]
             (binding [*in* r]
               (split (with-out-str (-main "--strict" "--testing")) #"\n")))]
       (is (re-matches expected-result result )))))
