#!/usr/bin/env clj
; Dance

(require '[clojure.string :as s]
         '[clojure.set])

(defn read-ints [] (mapv read-string (s/split (read-line) #" ")))

(defn shift-vector [i n v]
  (-> (map #(mod (+ i %) n) v)
      (sort)
      (vec)))

(defn shift [i n vs]
  (-> (map #(shift-vector i n %) vs)
      (set)))

(defn xx [n vs]
  (set (map #(shift % n vs) (range n))))

(def combinations
  (memoize
    (fn [n]
      (cond
        (odd? n) #{}
        (= n 0)  #{}
        (= n 2)  #{[[0 1]]}
        :else    (let [cont (set (mapv vec (partition 2 (range n))))
                       size  (* 2 (quot n 4))
                       midl (combinations size)
                       c     (if (zero? (mod n 4)) #{} #{[(dec (/ n 2)) (- n 1)]})
                       comb (for [a midl b midl]
                              (clojure.set/union a (shift (/ n 2) n b) c))]
                   (->> (map #(xx n %) comb)
                        (apply clojure.set/union  #{cont (shift 1 n cont)})))))))

(dotimes [c (first (read-ints))]
  (let [[n g]   (read-ints)
        grudges (->> (repeatedly g read-ints)
                     (doall)
                     (map (comp vec sort))
                     (set))]
        ; combs   (->> (combinations n)
        ;              (remove #(seq (clojure.set/intersection grudges %))))]
   (flush)
   (printf "Case #%d: %s %s\n" (inc c) n g)))


(comment

  Case #18: (8 3) 4
  Case #19: (6 3) 5
  Case #20: (6 2) 4
  Case #29: (14 37) 0
  Case #33: (16 46) 11
  Case #34: (16 43) 9
  Case #40: (16 53) 43)
