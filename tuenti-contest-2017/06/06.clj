#!/usr/bin/env lein-exec
; The Tower

(use '[leiningen.exec :only (deps)])
(deps '[[aysylu/loom "0.5.4"]])

(require '[loom graph alg])
(require '[clojure.string :as s])

(defn read-ints [] (map read-string (s/split (read-line) #" ")))

(defn all-nodes [floors shortcuts]
  (->> shortcuts
       (reduce #(conj %1 (first %2) (second %2)) (hash-set 1 floors))
       (sort)))

(defn backward-nodes [nodes]
  (->> (reverse nodes)
       (partition 2 1)
       (map #(conj (vec %) 0))))

(defn years [[a b]]
  (* (+ a b -1) (- b a) 1/2))

(defn forward-nodes [nodes]
  (->> (partition 2 1 nodes)
       (map #(conj (vec %) (years %)))))

(defn remove-repeated [nodes]
  (->> nodes
       (map (fn [[u v c]] {[u v] c}))
       (apply merge-with min)
       (map flatten)))

(defn build-tower-graph [floors shortcuts]
  (let [nodes (all-nodes floors shortcuts)]
    (->> shortcuts
         (concat (forward-nodes nodes))
         (concat (backward-nodes nodes))
         (remove-repeated)
         (apply loom.graph/weighted-digraph))))

(dotimes [n (first (read-ints))]
  (let [[floors s] (read-ints)
        shortcuts  (repeatedly s read-ints)
        tower      (build-tower-graph floors shortcuts)
        [_ years]  (loom.alg/dijkstra-path-dist tower 1 floors)]
    (flush)
    (printf "Case #%d: %s\n" (inc n) years)))
