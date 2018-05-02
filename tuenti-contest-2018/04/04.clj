#!/usr/bin/env lein-exec
; Brave Knight

(use '[leiningen.exec :only (deps)])
(deps '[[aysylu/loom "0.6.0"]])

(require '[clojure.string :as s]
         '[loom.alg-generic :as loom])

(defn read-ints [] (map read-string (s/split (read-line) #" ")))
(defn find-pos [{:keys [m h w]} e]
  (first (for [y (range h) x (range w) :when (= e (get-in m [y x]))] [y x])))

(defn read-maze [h w] {:m (mapv vec (repeatedly h read-line)) :h h :w w})

(defn successors [{:keys [m h w]}] (fn [node]
  (let [trampoline (= \* (get-in m node))
        power      (partial * (if trampoline 2 1))
        onboard?   #(every? identity (map < [-1 -1] % [h w]))
        ground?    #(#{\S \P \D \. \*} (get-in m %))]
    (if (not (ground? node))
      '()
      (->> [[-2 -1] [-2 1] [2 1] [2 -1] [-1 2] [1 2] [1 -2] [-1 -2]]
           (map #(mapv power %))
           (map #(mapv + node %))
           (filter onboard?)
           (filter ground?))))))

(dotimes [c (first (read-ints))]
  (let [[h w]      (read-ints)
        maze       (read-maze h w)
        start      (find-pos maze \S)
        princess   (find-pos maze \P)
        exit       (find-pos maze \D)
        bf-path    (partial loom/bf-path (successors maze))
        s-p        (and start princess exit (bf-path start princess))
        p-e        (and s-p (bf-path princess exit))]
    (if (or (nil? s-p) (nil? p-e))
      (printf "Case #%d: IMPOSSIBLE\n" (inc c))
      (printf "Case #%d: %s\n" (inc c) (+ (count s-p) (count p-e) -2)))))
