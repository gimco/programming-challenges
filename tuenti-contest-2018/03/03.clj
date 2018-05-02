#!/usr/bin/env clj
; Scales

(require '[clojure.string :as s]
         '[clojure.set :as cs])

(def all-notes '(C C# D D# E F F# G G# A A# B))
(def same-note '{Cb B Db C# Eb D# E# F Fb E Gb F# Ab G# Bb A# B# C})
(def M (map #(= \x %) "x.x.xx.x.x.x"))
(def m (map #(= \x %) "x.xx.x.xx.x."))

(defn heptatonic [scale root]
  (->> (cycle all-notes)
       (drop-while #(not= root %))
       (map #(and %1 %2) scale)
       (remove false?)
       (cons 'none)))

(def note->scales
  (->> (for [scale '(M m) note all-notes]
         { (str scale note) (heptatonic (eval scale) note)})
       (apply merge)
       (mapcat (fn [[k v]] (map #(hash-map % #{k}) v)))
       (apply merge-with into)))

(defn read-int [] (read-string (read-line)))

(dotimes [c (read-int)]
  (let [n    (read-int)
        song   (if (zero? n) ['none] (map symbol (s/split (read-line) #" ")))
        song   (map #(get same-note % %) song)
        scales (->> (map note->scales song)
                    (apply cs/intersection)
                    sort)
        result (if (seq scales) (s/join " " scales) "None")]
    (printf "Case #%d: %s\n" (inc c) result)))
