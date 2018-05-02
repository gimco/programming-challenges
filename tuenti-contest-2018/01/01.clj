#!/usr/bin/env clj
; Waffle love

(require '[clojure.string :as s])

(defn read-ints [] (map read-string (s/split (read-line) #" ")))

(dotimes [c (first (read-ints))]
  (let [[n m] (read-ints)
        holes (* (dec n) (dec m))]
    (printf "Case #%d: %d\n" (inc c) holes)))