(defproject beowulf "0.2.0-SNAPSHOT"
  :description "An implementation of LISP 1.5 in Clojure"
  :url "http://example.com/FIXME"
  :license {:name "GPL-2.0-or-later"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.trace "0.7.10"]
                 [environ "1.1.0"]
                 [instaparse "1.4.10"]]
  :main ^:skip-aot beowulf.core
  :plugins [[lein-environ "1.1.0"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
