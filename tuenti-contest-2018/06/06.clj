#!/usr/bin/env clj
; Button Hero

(require '[clojure.string :as s])

(defn read-ints [] (map read-string (s/split (read-line) #" ")))

(defn calcule-note [[x l s p]]
  (let [start (/ x s)
        size  (/ l s)]
    {[start (+ start size)] p}))

(defn previous-max [n maxs]
  (apply max 0 (keep #(when (< (key %) n) (val %)) maxs)))

(defn max-score [notes]
  (loop [[[[start end] p] & ns] notes
         maxs                   (sorted-map)]
    (if (nil? start) (apply max (vals maxs))
        (let [prevmax (previous-max start maxs)
              points  (+ p prevmax)
              curr    (maxs end 0)]
          (if (> points curr)
            (recur ns (assoc maxs end points))
            (recur ns maxs))))))

(dotimes [c (first (read-ints))]
  (let [[n]   (read-ints)
        notes (->> (repeatedly n read-ints)
                   (map calcule-note)
                   (apply merge-with +)
                   (into (sorted-map))
                   (seq))
        score (max-score notes)]
   (printf "Case #%d: %s\n" (inc c) score)))
