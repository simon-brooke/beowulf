(defproject beowulf "0.2.0"
  :cloverage {:output "docs/cloverage"}
  :codox {:metadata {:doc "**TODO**: write docs"
                     :doc/format :markdown}
          :output-path "docs/codox"
          :source-uri "https://github.com/simon-brooke/beowulf/blob/master/{filepath}#L{line}"}
  :description "An implementation of LISP 1.5 in Clojure"
  :license {:name "GPL-2.0-or-later"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.trace "0.7.10"]
                 [environ "1.1.0"]
                 [instaparse "1.4.10"]]
  :main ^:skip-aot beowulf.core
  :plugins [[lein-cloverage "1.1.1"]
            [lein-codox "0.10.7"]
            [lein-environ "1.1.0"]]
  :profiles {:uberjar {:aot :all}}
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "v." "--no-sign"]
                  ["clean"]
                  ["codox"]
                  ["cloverage"]
                  ["uberjar"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]]

  :target-path "target/%s"
  :url "https://github.com/simon-brooke/the-great-game"
  )
