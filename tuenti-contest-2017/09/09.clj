#!/usr/bin/env clj
; The Supreme Scalextric Architect

(defn read-ints [] (map read-string (clojure.string/split (read-line) #" ")))

; Possible modifications on a closed circuit
(def modifications
  [ [0 0 2]
    [2 0 0]
    [2 0 1]
    [2 2 0]
    [2 2 1]
    [0 4 1]
    [0 4 2]
    [2 4 0]
    [2 6 0]
    [2 4 1]
    [0 8 0]
    [0 8 1]])

(defn apply-modifications [circuit]
  (->> modifications
      (map #(mapv - circuit %))
      (remove #(some neg? %))
      (sort-by #(apply + %))))

(defn sum [c] (apply + c))

(def grow-circuit
  (memoize
    (fn [circuitX]
      (loop [circuit  circuitX
             slices   (sum circuit)
             [c & cs] (apply-modifications circuit)]
        (if (nil? c) circuit
          (let [c (grow-circuit c)
                slices-c (sum c)]
            (cond
              (zero? slices-c) c
              (< slices-c slices) (recur c slices-c cs)
              :else (recur circuit slices cs))))))))

(defn simplify [s] (if (< 8 s) (+ 8 (rem s 8)) s))

(defn play-scalestrix [[s c d]]
  (let [total (+ s c d)
        c (- c 4)
        circuit (mapv simplify [s c d])]
    (if (neg? c) 0
      (- total (sum (grow-circuit circuit))))))

(dotimes [n (first (read-ints))]
  (printf "Case #%d: %s\n" (inc n) (play-scalestrix (read-ints))))
