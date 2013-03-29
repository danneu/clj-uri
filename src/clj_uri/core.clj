(ns clj-uri.core
  (:import [java.net.URI])
  (:refer-clojure :exclude [resolve])
  (:use [clojure.string :only [split]]))

; TODO: Doesn't yet handle query strings without pairs.
; I think example.com/?q -> "q" ->{:q ""}       <-- not yet working
;         example.com/?q= -> "q=" -> {:q ""}    <-- not yet working
;         example.com/?q=a -> "q=a" -> {:q "a"} <-- works
(defn query->map [query-str]
  "Parse query string into hash-map.

   Example: 'a=hello&b=world' into {:a 'hello' :b 'world'}"
  ; (re-seq #"\A(.)[^=]?+|&(.)" "a") ;<-- a better split I'll incorporate.
  (let [pairs (split query-str #"=|&")]
    (apply hash-map
           (map #(%1 (or %2 ""))
                (cycle [keyword identity])
                pairs))))

; The purpose of the following code is so that we can pass either
; URIs or Strings into our method wrappers and Strings will first
; be parsed into URIs.

(defmulti to-uri
  "Parses a String into a java.net.URI object.

   It's applied to all args of our wrapper functions so that
   you can just pass in Strings and they'll be converted for you."
  class)
(defmethod to-uri String [uri-str] (java.net.URI/create uri-str))
(defmethod to-uri java.net.URI [uri] uri)

(defn ensure-uri-args [f]
  "Returns a function where to-uri is first applied to the args
   of wrapped function f before evaluated by f."
  (fn [& args]
    (apply f (map to-uri args))))

(defmacro def-uri-method
  [clojure-fn-name doc-string args & body]
  `(def ~(with-meta clojure-fn-name
                    (assoc (meta name) :doc doc-string))
     (ensure-uri-args (fn ~args ~@body))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def-uri-method path
  "Returns the path component."
  [uri]
  (.getPath uri))

(def-uri-method host
  "Returns the host component."
  [uri]
  (.getHost uri))

(def-uri-method port
  "Returns port integer or nil."
  [uri]
  (let [port (.getPort uri)]
    (when (pos? port) port)))

(def-uri-method query
  "Returns query component."
  [uri]
  (.getQuery uri))

(def-uri-method query-map
  "Parses query string into hash-map.
   Example: 'a=hello&b=world' to {:a 'hello' :b 'world'}"
  [uri]
  ((comp query->map query) uri))

(def-uri-method raw-authority
  "Returns raw authority component.

   Does not decode %-escaped octets."
  [uri]
  (.getRawAuthority uri))

(def-uri-method authority
  "Returns authority component.

   Decodes %-escaped octets."
  [uri]
  (.getAuthority uri))

(def-uri-method fragment
  "Returns fragment component.

   Decodes %-escaped octets."
  [uri]
  (.getFragment uri))

(def-uri-method raw-fragment
  "Returns raw fragment component.

   Does not decode %-escaped octets."
  [uri]
  (.getRawFragment uri))

(def-uri-method raw-path
  "Returns raw path component.

   Does not decode %-escaped octets."
  [uri]
  (.getRawPath uri))

(def-uri-method raw-query
  "Returns raw query component.

   Does not decode %-escaped octets."
  [uri]
  (.getRawQuery uri))

(def-uri-method raw-scheme-specific-part
  "Returns raw scheme specific part component.

   Never nil.

   Does not decode %-escaped octets."
  [uri]
  (.getRawSchemeSpecificPart uri))

(def-uri-method raw-user-info
  "Returns raw user info component.

   Does not decode %-escaped octets."
  [uri]
  (.getRawUserInfo uri))

(def-uri-method scheme
  "Returns scheme component or nil."  [uri]
  (.getScheme uri))

(def-uri-method scheme-specific-part
  "Returns the raw-scheme-specific-part with the %-escaped octets
   decoded.

   Never nil."
  [uri]
  (.getSchemeSpecificPart uri))

(def-uri-method user-info
  "Returns 'user:password' string or nil.

   Note: http://<user>:<password>@example.com
         user-info is '<user>:<password>'"
  [uri]
  (.getUserInfo uri))

(def-uri-method user-info-map
  "Returns hash-map {:user <user> :password <password>} or nil."
  [uri]
  (when-let [info (user-info uri)]
    (let [split-info (split info #":")
          [user password] split-info]
      {:user (or user "")
       :password (or password "")})))

(def-uri-method hash-code
  "Returns hash code component based upon all of the URI's
   components."
  [uri]
  (.hashCode uri))

(def-uri-method normalize
  "Returns normalized URI.

   - /./ segments are removed
   - /non-../../ segments are removed
   - If path is relative and if its first segment contains colon,
     then a /./ segment is prepended."
  [uri] (.normalize uri))

(def-uri-method server-authority
  "Attempts to parse this URI's authority component, if defined,
   into user-info, host, and port components."
  [uri] (.parseServerAuthority uri))

(def-uri-method to-ascii-string
  "Returns string representation with non-US-ASCII chars encoded."
  [uri] (.toASCIIString uri))

(def-uri-method to-string
  "Returns string representation as-is."
  [uri] (.toString uri))

(def-uri-method to-url
  "Constructs java.net.URL from this URI."
  [uri] (.toURL uri))

; Predicates

(def-uri-method absolute?
  "A URI is absolute iff it has a scheme component."
  [uri] (.isAbsolute uri))

(def-uri-method opaque?
  "A URI is opaque iff it is absolute and its scheme-specific
   part does not begin with a slash character ('/').

   An opaque URI:
   - Has a scheme
   - Has a scheme-specific part
   - Possibly has a fragment
   - All other components are undefined"
  [uri]
  (.isOpaque uri))

; Multi-args

(def-uri-method equals
  "Tests for equality."
  [this-uri that-uri]
  (.equals this-uri that-uri))

(def-uri-method compare-to
  "A negative integer, zero, or a positive integer as this URI
   is less than, equal to, or greater than that URI."
  [this-uri that-uri]
  (.compareTo this-uri that-uri))

(def-uri-method relativize
  "Relativizes that-uri against this-uri. If it can't, then it just
   returns that-uri.

   Example: (relativize http://example.com
                        http://example.com/about.php)
            ;=> #<URI about.php>"
  [this-uri that-uri]
  (.relativize this-uri that-uri))

(def-uri-method resolve
  "Constructs a new URI by parsing the given string and then
   resolving it against this URI."
  [this-uri that-uri]
  (.resolve this-uri that-uri))






