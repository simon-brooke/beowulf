(defproject beowulf "0.1.0-SNAPSHOT"
  :description "An implementation of LISP 1.5 in Clojure"
  :url "http://example.com/FIXME"
  :license {:name "GPL-2.0-or-later"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [instaparse "1.4.10"]]
  :main ^:skip-aot beowulf.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
