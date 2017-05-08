#!/usr/bin/env lein-exec
; Colors

(use '[leiningen.exec :only (deps)])
(deps '[[aysylu/loom "0.5.4"]])

(require '[loom graph alg])
(require '[clojure.string :as s])

(defn read-int [] (read-string (read-line)))
(defn process [f]
  (->> (repeatedly (read-int) read-line)
       (map #(s/split % #" "))
       (reduce f {})))

(defn colors-data [c [x _ & xs]] (assoc c x (or xs (list x))))
(defn galaxy-data [g [x t]] (assoc g x (read-string t)))
(defn wormholes-data [g [x a b]] (update-in g (mapv read-string [a b]) conj x))
(defn process-galaxies-data [] (vec (repeatedly (read-int) (partial process galaxy-data))))

(defn absorb-energy [colors g]
  (let [ks [:energy (:galaxy g)]]
    (->> (get-in g ks)
         (map (fn [[color seconds]]
                (-> g
                   (update :time + seconds)
                   (update :myself (partial apply conj) (colors color))))))))

(defn wormholes-travel [colors wormholes {:keys [galaxy myself] :as g}]
  (->> (get wormholes galaxy)
       (mapcat (fn [[destiny cs]] (mapv (partial vector destiny) cs)))
       (filter #(every? myself (colors (second %))))
       (map (fn [[destiny color]]
              (-> g
                 (assoc :galaxy destiny)
                 (update :myself (partial apply disj) (colors color)))))))

(defn make-successors [colors wormholes]
  (fn [g]
    (concat (absorb-energy colors g) (wormholes-travel colors wormholes g))))

(defn explore-universe [n successors galaxy0]
  (loop [min-times (vec (repeat n Double/POSITIVE_INFINITY))
         visited #{}
         [{:keys [galaxy time] :as current} & exploring] (list galaxy0)]
    (println "loop" "explorando" galaxy "pendientes" (count exploring) "visitados" (count visited))
    (println (:galaxy current) (:myself current))
    (cond
      (nil? current) min-times
      (> time (apply max min-times)) min-times
      (visited ((juxt :galaxy :myself) current)) (recur min-times visited exploring)
      :else (recur
              (update min-times galaxy min time)
              (conj visited ((juxt :galaxy :myself) current))
              (sort-by :time (concat (or exploring '()) (successors current)))))))

(dotimes [n (read-int)]
  (let [colors     (process colors-data)
        energy     (process-galaxies-data)
        wormholes  (process wormholes-data)
        galaxy0    {:galaxy 0 :myself #{} :time 0 :energy energy}
        min-times  (explore-universe (count energy) (make-successors colors wormholes) galaxy0)
        min-times  (mapv #(if (= Double/POSITIVE_INFINITY %) -1 %) min-times)]
    (printf "Case #%d: %s\n" (inc n) (s/join " " min-times))
    (flush)))
