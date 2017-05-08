#!/usr/bin/env clj
; Bowling

(require '[clojure.string :as s])

(defn read-ints [] (map read-string (s/split (read-line) #" ")))

(defn bowling-score [rolls]
  (loop [[score & _ :as scores]   '(0)
         [r1 r2 r3 & _ :as rolls] rolls]
    (cond
      (= 11 (count scores)) (-> scores reverse next)
      (= 10 r1)             (recur (conj scores (+ score r1 r2 r3)) (next rolls))
      (= 10 (+ r1 r2))      (recur (conj scores (+ score r1 r2 r3)) (nnext rolls))
      :else                 (recur (conj scores (+ score r1 r2)) (nnext rolls)))))

(dotimes [n (first (read-ints))]
  (let [_      (read-ints)
        rolls  (read-ints)
        scores (bowling-score rolls)]
    (printf "Case #%d: %s\n" (inc n) (s/join " " scores))))
