(ns beowulf.core-test
  (:require [clojure.java.io :refer [reader]]
            [clojure.string :refer [split]]
            [clojure.test :refer :all]
            [beowulf.core :refer :all]))

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
      (binding [*in* r]
        (is (thrown-with-msg? Exception #"\nFærwell!" (repl "")))))

    (let [expected nil
          actual (with-open [r (reader (string->stream stop-word))]
                   (binding [*in* r]
                     (-main)))]
      (is (= actual expected)))))

;; TODO: not working because STOP is not being recognised, but I haven't
;; worked out why not yet. It *did* work.

(deftest flag-tests
  (testing "No flags"
    (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
          expected-quit-message (str "Sprecan '" stop-word "' tó laéfan")
          expected-result #".*\(A \. B\)"
          expected-prompt "Sprecan:: "
          expected-signoff "Færwell!"
          ;; anticipated output (note blank lines):
          
          ; Hider wilcuman. Béowulf is mín nama.
          
          ; Sprecan 'STOP' tó laéfan
          
          ; Sprecan:: >  (A . B)
          ; Sprecan:: 
          ; Færwell!
          [_ greeting _ _ quit-message _ result prompt signoff]
          (with-open [r (reader (string->stream (str "cons[A; B]\n" stop-word)))]
            (binding [*in* r]
              (split (with-out-str (-main)) #"\n")))]
      (is (= greeting expected-greeting))
      ; (is (= error expected-error))
      (is (re-matches expected-result result))
      (is (= quit-message expected-quit-message))
      (is (= prompt expected-prompt))
      (is (= signoff expected-signoff))
      ))
  (testing "unknown flag"
    (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
          expected-quit-message (str "Sprecan '" stop-word "' tó laéfan")
          expected-error #"Unknown option:.*"
          expected-result #".*\(A \. B\)"
          expected-prompt "Sprecan:: "
          expected-signoff "Færwell!"
          [_ greeting _ error quit-message _ result prompt signoff]
          (with-open [r (reader (string->stream (str "cons[A; B]\n" stop-word)))]
            (binding [*in* r]
              (split (with-out-str (-main "--unknown")) #"\n")))]
      (is (= greeting expected-greeting))
      (is (re-matches expected-error error))
      (is (re-matches expected-result result))
      (is (= quit-message expected-quit-message))
      (is (= prompt expected-prompt))
      (is (= signoff expected-signoff))
      ))
  ; (testing "help"
  ;   (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
  ;         expected-h1 "  -h, --help"
  ;         expected-quit-message (str "Sprecan '" stop-word "' tó laéfan")
  ;         expected-result #".*\(A \. B\)"
  ;         expected-prompt "Sprecan:: "
  ;         expected-signoff "Færwell!"
  ;         [_ greeting _ h1 _ _ _ _ quit-message _ result prompt signoff]
  ;         (with-open [r (reader (string->stream (str "cons[A; B]\n" stop-word)))]
  ;                  (binding [*in* r]
  ;                    (split (with-out-str (-main "--help")) #"\n")))]
  ;     (is (= greeting expected-greeting))
  ;     (is (= h1 expected-h1))
  ;     (is (re-matches expected-result result))
  ;     (is (= quit-message expected-quit-message))
  ;     (is (= prompt expected-prompt))
  ;     (is (= signoff expected-signoff))
  ;     ))
  ; (testing "prompt"
  ;   (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
  ;         expected-quit-message (str "Sprecan '" stop-word "' tó laéfan")
  ;         expected-error ""
  ;         expected-result #".*\(A \. B\).*"
  ;         expected-prompt "? "
  ;         expected-signoff "Færwell!"
  ;         [_ greeting _ error quit-message _ result prompt signoff]
  ;         (with-open [r (reader (string->stream (str stop-word)))]
  ;           (binding [*in* r]
  ;             (split (with-out-str (-main "--prompt" "?")) #"\n")))]
  ;     (is (= greeting expected-greeting))
  ;     (is (= error expected-error))
  ;     (is (re-matches expected-result result ))
  ;     (is (= quit-message expected-quit-message))
  ;     (is (= prompt expected-prompt))
  ;     (is (= signoff expected-signoff))
  ;     ))
  ; (testing "read - file not found"
  ;   (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
  ;         expected-quit-message (str "Sprecan '" stop-word "' tó laéfan")
  ;         expected-error #"Failed to validate.*"
  ;         expected-result #".*\(A \. B\)"
  ;         expected-prompt "Sprecan:: "
  ;         expected-signoff "Færwell!"
  ;         [_ greeting _ error quit-message _ result prompt signoff]
  ;         (with-open [r (reader (string->stream (str "cons[A; B]\n" stop-word)))]
  ;           (binding [*in* r]
  ;             (split (with-out-str (-main "--read" "froboz")) #"\n")))]
  ;     (is (= greeting expected-greeting))
  ;     (is (re-matches expected-error error))
  ;     (is (re-matches expected-result result))
  ;     (is (= quit-message expected-quit-message))
  ;     (is (= prompt expected-prompt))
  ;     (is (= signoff expected-signoff))
  ;     ))
  ; (testing "read - file found"
  ;   ;; TODO: there's no feedback from this because the initfile
  ;   ;; is not yet read. This will change
  ;   (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
  ;         expected-quit-message (str "Sprecan '" stop-word "' tó laéfan")
  ;         expected-error ""
  ;         expected-result #".*\(A \. B\)"
  ;         expected-prompt "Sprecan:: "
  ;         expected-signoff "Færwell!"
  ;         [_ greeting error quit-message _ _ result prompt signoff]
  ;         (with-open [r (reader (string->stream (str "cons[A; B]\n" stop-word)))]
  ;           (binding [*in* r]
  ;             (split (with-out-str (-main "--read" "README.md")) #"\n")))]
  ;     (is (= greeting expected-greeting))
  ;     (is (= error expected-error))
  ;     (is (re-matches expected-result result))
  ;     (is (= quit-message expected-quit-message))
  ;     (is (= prompt expected-prompt))
  ;     (is (= signoff expected-signoff))
  ;     ))
  ; (testing "strict"
  ;   (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
  ;         expected-quit-message (str "Sprecan '" stop-word "' tó laéfan")
  ;         expected-error ""
  ;         expected-result #".*Cannot parse meta expressions in strict mode.*"
  ;         expected-prompt "Sprecan:: "
  ;         expected-signoff "Færwell!"
  ;         [_ greeting _ error quit-message _ result prompt signoff]
  ;         (with-open [r (reader (string->stream (str "cons[A; B]\n" stop-word)))]
  ;           (binding [*in* r]
  ;             (split (with-out-str (-main "--strict")) #"\n")))]
  ;     (is (= greeting expected-greeting))
  ;     (is (= error expected-error))
  ;     (is (re-matches expected-result result ))
  ;     (is (= quit-message expected-quit-message))
  ;     (is (= prompt expected-prompt))
  ;     (is (= signoff expected-signoff))
  ;     ))
  ; ; (testing "trace"
  ;   (let [expected-greeting "Hider wilcuman. Béowulf is mín nama."
  ;         expected-error ""
  ;         expected-trace #".*traced-eval.*"
  ;         [_ greeting _ error _ _ trace & _]
  ;         (with-open [r (reader (string->stream (str "cons[A; B]\n" stop-word)))]
  ;           (binding [*in* r]
  ;             (split (with-out-str (-main "--trace")) #"\n")))]
  ;     (is (= greeting expected-greeting))
  ;     (is (= error expected-error))
  ;     (is (re-matches expected-trace trace))
)