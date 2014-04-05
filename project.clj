(defproject backgammon "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :resource-paths ["resources"]
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2197"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                 [liberator "0.11.0"]
                 [compojure "1.1.3"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [om "0.5.3"]]
  :plugins [[lein-ring "0.8.10"]
            [lein-cljsbuild "1.0.3"]]
  :ring {:handler backgammon.server/index}
  :cljsbuild {
    :builds [{:id "dev"
            :source-paths ["src"]
            :compiler {
              :output-to "resources/public/main.js"
              :output-dir "resources/public/out"
              :optimizations :none
              :source-map true}}]})
