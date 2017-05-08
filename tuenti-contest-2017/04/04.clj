#!/usr/bin/env clj
; Help Pythagoras Junior

(require '[clojure.string :as s])

(defn read-ints [] (map read-string (s/split (read-line) #" ")))

(defn smallest-triangle [sides]
  (let [sides (sort sides)]
    (loop [[a & as :as A] sides
           [b & bs :as B] (next sides)
           [c & cs :as C] (nnext sides)
           [smallest last-c :as t]  [nil (last sides)]]
      (cond
        (nil? a) smallest
        (nil? b) (recur as (next as) (nnext as) t)
        (nil? c) (recur A bs (next bs) t)
        (> b last-c) (recur as (next as) (nnext as) t)
        (<= (+ a b) c) (recur A B cs t)
        (or (nil? smallest) (> smallest (+ a b c))) (recur A bs (next bs) [(+ a b c) c])
        :else (recur A bs (next bs) t)))))

(dotimes [n (first (read-ints))]
  (let [[_ & sides] (read-ints)
        smallest (smallest-triangle sides)]
    (printf "Case #%d: %s\n" (inc n) (str (or smallest "IMPOSSIBLE")))))
