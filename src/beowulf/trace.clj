(ns beowulf.trace
  "Tracing of function execution")

(def traced-symbols
  "Symbols currently being traced."
  (atom #{}))

(defn traced?
  "Return `true` iff `s` is a symbol currently being traced, else `nil`."
  [s]
  (try (contains? @traced-symbols s)
       (catch Throwable _)))

(defn TRACE
  "Add this symbol `s` to the set of symbols currently being traced. If `s`
   is not a symbol, does nothing."
  [s]
  (when (symbol? s)
    (swap! traced-symbols #(conj % s))))

(defn UNTRACE
  [s]
  (when (symbol? s)
    (swap! traced-symbols #(set (remove (fn [x] (= s x)) %)))))