#!/usr/bin/env clj
; Pizza love

(require '[clojure.string :as s])

(defn read-ints [] (map read-string (s/split (read-line) #" ")))

(dotimes [n (first (read-ints))]
  (let [_ (read-ints)
        slices (reduce + (read-ints))
        pizzas (int (Math/ceil (/ slices 8)))]
    (printf "Case #%d: %d\n" (inc n) pizzas)))