(defproject kibit-cljs "0.1.1"
  :description "Clojurescript port of https://github.com/jonase/kibit"
  :url "https://github.com/bbbates/kibit-cljs"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "Contact if any questions"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.logic "0.8.7"]]
  :profiles {:dev {:dependencies [[lein-marginalia "0.8.0"]]
                   :resource-paths ["test/resources"]}}
  :deploy-repositories [["releases" :clojars]]
  :warn-on-reflection false)
