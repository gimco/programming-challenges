#!/usr/bin/env clj
; Board games

(defn read-int [] (read-string (read-line)))

(dotimes [n (read-int)]
  (let [points (read-int)
        cards  (->> (iterate (partial * 2) 1)
                    (take-while #(<= % points))
                    (count))]
    (printf "Case #%d: %d\n" (inc n) cards)))
