# clj-uri

A Clojure library for parsing URI strings into their URI components.

`clj-uri` is a Clojure wrapper around `java.net.URI` that adds some aditional conveniences.

## Install

Add to `project.clj` dependencies:

    [clj-uri "0.1.0"]

## Usage

``` clojure
(ns my-namespace.core
  (:require [clj-uri :as uri]))

(def uri-str "http://danneu:secret@my.example.com:8080/some/stuff.php?a=foo&b=bar#yolo")
(def uri (to-uri uri-str))

(uri/scheme uri)          ;=> "http"
(uri/path uri)            ;=> "/some/stuff.php"
(uri/port uri)            ;=> 8080
(uri/query uri)           ;=> "a=foo&b=bar"
(uri/query-map uri)       ;=> {:a "foo" :b "bar"}
(uri/host uri)            ;=> "my.example.com"
(uri/user-info uri)       ;=> "danneu:secret"
(uri/user-info-map uri)   ;=> {:user "danneu" :password "secret"
(uri/fragment uri)        ;=> "yolo"
(uri/absolute? "/me.php") ;=> false
; and more...
```

For convenience, `clj-uri` will apply `to-uri` to wrapper arguments so you don't have to manually parse them into URIs yourself.

``` clojure
(uri/to-uri "http://example.com") ;=> #<URI "http://example">
(uri/scheme (uri/to-uri "http://example.com")) ;=> "http"
(uri/scheme "http://example")                  ;=> "http"
```

See the tests for more examples.

## Deviations from `java.net.URI`

* Don't need to parse Strings into URIs first.

  `clj-uri` applies `to-uri` to arguments for you.

  ``` clojure
  (port (to-uri "http://example.com:8080")) ;=> 8080
  (port "http://example.com:8080") ;=> 8080
  ```

* Port can be nil

  * `clj-uri.core/port` returns a port integer or nil.
  * `java.net.URI#getPort` returns a port integer or -1.
  
* Additional functions

  * `clj-uri.core/query-map` parses query string into a hash-map.
   
      ``` clojure
      (query-map "http://example.com?a=foo&b=bar")
      ;=> {:a "foo" :b "bar"}
      ```

  * `clj-uri.core/user-info-map` parses user─info into a hash-map.
  
      ``` clojure
      (user-info "http://danneu:secret@example.com")
      ;=> "danneu:secret"

      (user-info-map "http://danneu:secret@example.com")
      ;=> {:user "danneu" :password "secret"}
      ```

## Generate the docs

From project root, run:

    $ lein doc

Now you can open the generated `doc/index.html`.

## Run the tests

Since `clj-uri` is a light wrapper around `java.net.URI`, most of
the tests are just sanity checks to ensure the wrapper indeed
wraps the underlying library.

However, I particularly want to test deviations from `java.net.URI`.

* `lein midje` will run all tests.
* `lein midje namespace.*` will run only tests beginning with "namespace.".
* `lein midje :autotest` will run all the tests indefinitely. 
It sets up a watcher on the code files. If they change, only the 
relevant tests will be run again.
