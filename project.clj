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
                 [clj-jade "0.1.4"]
                 [compojure "1.1.6"]
                 [om "0.5.3"]
                 [com.cemerick/clojurescript.test "0.3.0"]]
  :plugins [[lein-ring "0.8.10"]
            [lein-cljsbuild "1.0.3"]
            [com.cemerick/clojurescript.test "0.3.0"]]
  :ring {:handler backgammon.server/index}
  :cljsbuild {
    :builds { :dev
              { :source-paths ["src"]
                :compiler {
                  :output-to "resources/public/main.js"
                  :output-dir "resources/public/out"
                  :optimizations :none
                  :source-map true }}
               :tests
               { :source-paths ["src" "test"]
                 :compiler {
                  :output-to "target/test/test.js"
                  :pretty-print true }
                 :notify-command ["phantomjs" :cljs.test/runner "test/react-stub.js" "target/test/test.js"]
                }}
    :test-commands {"unit-tests" ["phantomjs" :runner "test/react-stub.js" "target/test/test.js"]}})
