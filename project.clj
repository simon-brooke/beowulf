(defproject beowulf "0.3.1-SNAPSHOT"
  :aot :all
  :cloverage {:output "docs/cloverage"
              :ns-exclude-regex [#"beowulf\.gendoc" #"beowulf\.scratch"]}
  :codox {:html {:transforms [[:head] [:append 
                                       [:link {:rel "icon"
                                               :type "image/x-icon"
                                               :href "../img/beowulf_logo_favicon.png"}]]]}
          :metadata {:doc "**TODO**: write docs"
                     :doc/format :markdown}
          :output-path "docs/codox"
          :source-uri "https://github.com/simon-brooke/beowulf/blob/master/{filepath}#L{line}"
          ;; :themes [:journeyman]
          }
  :description "LISP 1.5 is to all Lisp dialects as Beowulf is to English literature."
  :license {:name "GPL-2.0-or-later"
            :url "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/math.combinatorics "0.2.0"] ;; not needed in production builds
                 [org.clojure/math.numeric-tower "0.0.5"]
                 [org.clojure/tools.cli "1.0.214"]
                 [org.clojure/tools.trace "0.7.11"]
                 [clojure.java-time "1.2.0"]
                 [environ "1.2.0"]
                 [instaparse "1.4.12"]
;;                 [org.jline/jline "3.23.0"]
                 [rhizome "0.2.9"] ;; not needed in production builds
                 ]
  :main  beowulf.core
  :plugins [[lein-cloverage "1.2.2"]
            [lein-codox "0.10.7"]
            [lein-environ "1.1.0"]]
  :profiles {:jar {:aot :all}
             :uberjar {:aot :all}}
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "v." "--no-sign"]
                  ["clean"]
                  ["codox"]
                  ["uberjar"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]]
  :target-path "target/%s"
  :url "https://github.com/simon-brooke/the-great-game")
