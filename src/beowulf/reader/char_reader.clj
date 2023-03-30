(ns beowulf.reader.char-reader
  "Provide sensible line editing, auto completion, and history recall.
   
   None of what's needed here is really working yet, and a pull request with
   a working implementation would be greatly welcomed.
   
   ## What's needed (rough specification)
   
   1. Carriage return **does not** cause input to be returned, **unless**
       a. the number of open brackets `(` and closing brackets `)` match; and
       b. the number of open square brackets `[` and closing square brackets `]` also match;
   2. <Ctrl-D> aborts editing and returns the string `STOP`;
   3. <Up-arrow> and <down-arrow> scroll back and forward through history, but ideally I'd like 
      this to be the Lisp history (i.e. the history of S-Expressions actually read by `READ`, 
      rather than the strings which were supplied to `READ`);
   4. <Tab> offers potential auto-completions taken from the value of `(OBLIST)`, ideally the
      current value, not the value at the time the session started;
   5. <Back-arrow> and <Forward-arrow> offer movement and editing within the line."
  (:import [org.jline.reader LineReader LineReaderBuilder]
           [org.jline.terminal TerminalBuilder]))

;; It looks from the example given [here](https://github.com/jline/jline3/blob/master/demo/src/main/java/org/jline/demo/Repl.java)
;; as though JLine could be used to build a perfect line-reader for Beowulf; but it also
;; looks as though you'd need a DPhil in JLine to write it, and I don't have
;; the time.

(def get-reader
  "Return a reader, first constructing it if necessary.
   
   **NOTE THAT** this is not settled API. The existence and call signature of
   this function is not guaranteed in future versions."
  (memoize (fn []
  (let [term (.build (.system (TerminalBuilder/builder) true))]
    (.build (.terminal (LineReaderBuilder/builder) term))))))

(defn read-chars
  "A drop-in replacement for `clojure.core/read-line`, except that line editing
   and history should be enabled.
   
   **NOTE THAT** this does not work yet, but it is in the API because I hope 
   that it will work later!"
  [] 
    (let [eddie (get-reader)]
      (loop [s (.readLine eddie)]
      (if (and (= (count (re-seq #"\(" s))
           (count (re-seq #"\)" s)))
               (= (count (re-seq #"\[]" s))
                  (count (re-seq #"\]" s))))
        s
        (recur (str s " " (.readLine eddie)))))))