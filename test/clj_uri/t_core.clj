(ns clj-uri.t-core
  (:refer-clojure :exclude [resolve])
  (:use midje.sweet)
  (:use [clj-uri.core]))

(facts "about path"
  (path "http://example.com/path/to.php") => "/path/to.php")

(facts "about host"
  (host "http://example.com/yolo") => "example.com")

(facts "about port"
  (port "http://example.com") => nil
  (port "http://example.com:8080") => 8080)

(facts "about query"
  (query "//example.com?q") => "q"
  (query "//example.com?q=") => "q="
  (query "//example.com?a=hello&b=world") => "a=hello&b=world")

(facts "about query-map"
  (query-map "//example.com?a=hello&b=world") => {:a "hello"
                                                  :b "world"})
;;   (query-map "//x.com/?q") => {:q ""}
;;   (query-map "//x.com/?q=") => {:q ""})

(facts "about absolute?"
  (absolute? "there.php") => false
  (absolute? "http://example.com/path/to/there.php") => true)

(facts "about opaque?"
  (opaque? "mailto:me@example.com") => true
  (opaque? "//example.com") => false)

(facts "about scheme"
  (scheme "http://example.com") => "http"
  (scheme "ftp://example.com") => "ftp"
  (scheme "//example.com") => nil)

(facts "about user-info"
  (user-info "//danneu:secret@example.com") => "danneu:secret"
  (user-info "//danneu:@example.com") => "danneu:"
  (user-info "//danneu@example.com") => "danneu"
  (user-info "//:secret@example.com") => ":secret"
  (user-info "//:@example.com") => ":"
  (user-info "//@example.com") => ""
  (user-info "//%2C:@example.com") => ",:")

(facts "about raw-user-info"
  (raw-user-info "http://%2C:@example.com") => "%2C:")

(facts "about user-info-map"
  (user-info-map "//danneu:secret@example.com") =>
  {:user "danneu" :password "secret"}
  (user-info-map "//:secret@example.com") =>
  {:user "" :password "secret"}
  (user-info-map "//:@example.com") => {:user "" :password ""}
  (user-info-map "//@example.com") => {:user "" :password ""}
  (user-info-map "//example.com") => nil)

(facts "about hash-code"
  (hash-code "http://example.com") => -1638917912)

(facts "about to-ascii-string"
  (to-ascii-string "http://example.com?utf8=✓") =>
    "http://example.com?utf8=%E2%9C%93")

(facts "about to-string"
  (to-string "http://example.com?utf8=✓") =>
    "http://example.com?utf8=✓")

(facts "about to-url"
  (class (to-url "http://example.com")) => java.net.URL)

(facts "about server-authority"
  (server-authority "http://example.com") =>
  (to-uri "http://example.com"))

(facts "about normalize"
  (to-string (normalize "http://example.com/./hello.php")) =>
  "http://example.com/hello.php")

(facts "about scheme-specific-part"
  (scheme-specific-part
   "http://example.com?q=hello%2C%20world!")
  => "//example.com?q=hello, world!")

(facts "about raw-scheme-specific-part"
  (raw-scheme-specific-part
   "http://example.com%2F%3F%3Dhello%2C%20world")
  => "//example.com%2F%3F%3Dhello%2C%20world")

(facts "about query"
  (query "http://example.com?q=hello%2C%20world") =>
  "q=hello, world")

(facts "about raw-query"
  (raw-query "http://example.com?q=hello%2C%20world") =>
  "q=hello%2C%20world")

(facts "about path"
  (path "http://example.com/hello%2C/world") => "/hello,/world")

(facts "about raw-path"
  (raw-path "http://example.com/hello%2C/world") =>
  "/hello%2C/world")

(facts "about fragment"
  (fragment "http://example.com#foo%2Cbar") => "foo,bar")

(facts "about raw-fragment"
  (raw-fragment "http://example.com#foo%2Cbar") => "foo%2Cbar")

(facts "about authority"
  (authority "http://%2Cexample.com") => ",example.com")

(facts "about raw-authority"
  (raw-authority "http://%2Cexample.com") => "%2Cexample.com")

(facts "about relativize"
  (relativize "http://example.com"
              "http://example.com/about") => (to-uri "about"))

(facts "about compare-to"
  (compare-to "http://example.com" "http://example.com") => 0
  (compare-to "http://example.com" "ftp://example.com") => 2)

(facts "about equals"
  (equals "http://example.com" "http://example.com") => true
  (equals "http://example.com" "http://danneu.com") => false)