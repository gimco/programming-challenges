#!/usr/bin/env clj
; Hidden numbers

(defn max-difference [base]
  (let [max-digits (range base)
        min-digits (reverse (conj (range 2 base) 0 1))
        components (map #(.pow (biginteger base) %) max-digits)
        vmax       (reduce + (map * components max-digits))
        vmin       (reduce + (map * components min-digits))]
    (- vmax vmin)))

(dotimes [t (read-string (read-line))]
  (let [s (read-line)
        v (max-difference (count s))]
    (printf "Case #%d: %s\n" (inc t) v)))
