(defproject clj-uri "0.1.0-SNAPSHOT"
  :description "Parse URI strings into URI components. (wraps java.net.URI)"
  :url "http://github.com/danneu/clj-uri"
  :plugins [[codox "0.6.4"]]
  :profiles {:dev {:dependencies [[midje "1.5.0"]]}}
  :dependencies [[org.clojure/clojure "1.4.0"]])