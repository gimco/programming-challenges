#!/usr/bin/env clj
; Uni code to rule them all

(import '(java.io InputStreamReader BufferedReader))

(def input (BufferedReader. (InputStreamReader. System/in "UTF-16LE")))
(defn readline [] (.readLine input))

(def valid-number #"\p{Zs}*(\p{Nd}+)\p{Zs}*")

(.read input) ; skip BOM
(dotimes [n (read-string (readline))]
  (if-let [[_ t] (re-matches valid-number (readline))]
    (printf "Case #%d: %s\n" (inc n) (.toString (biginteger t) 16))
    (printf "Case #%d: N/A\n" (inc n))))
