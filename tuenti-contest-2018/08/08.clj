#!/usr/bin/env lein-exec
; Security by doorscurity

(use '[leiningen.exec :only (deps)])
(deps '[[org.clojure/math.combinatorics "0.1.4"]
        [org.clojure/math.numeric-tower "0.0.4"]])

(require '[clojure.math.numeric-tower :as math]
         '[clojure.math.combinatorics :as c]
         '[clojure.string :as s])

(defn read-ints [] (map (comp biginteger read-string) (s/split (read-line) #" ")))

; https://rosettacode.org/wiki/Chinese_remainder_theorem#Clojure
(defn extended-gcd [a b]
  (cond (zero? a) [(math/abs b) 0 1]
        (zero? b) [(math/abs a) 1 0]
        :else (loop [s 0
                     s0 1
                     t 1
                     t0 0
                     r (math/abs b)
                     r0 (math/abs a)]
                (if (zero? r)
                  [r0 s0 t0]
                  (let [q (quot r0 r)]
                    (recur (- s0 (* q s)) s
                           (- t0 (* q t)) t
                           (- r0 (* q r)) r))))))

(defn chinese-remainder [equs]
  (let [[n a]   (apply map vector equs)
        prod    (apply * n)
        reducer (fn [sum [n_i a_i]]
                  (let [p (quot prod n_i)
                        egcd (extended-gcd p n_i)
                        inv_p (second egcd)]
                    (+ sum (* a_i inv_p p))))
        sum-prod (reduce reducer 0 (map vector n a))]
    (mod sum-prod prod)))

(defn coprimes [[a _] [b _]] (= 1 (first (extended-gcd a b))))

; https://math.stackexchange.com/questions/120070/chinese-remainder-theorem-with-non-pairwise-coprime-moduli
(defn simplyfy [[a ar] [b br]]
  (let [[c _ _] (extended-gcd a b)
        cr      (mod (- ar br) c)
        aa      (/ a c)
        bb      (/ b c)
        [d _ _] (extended-gcd aa c)]
    (cond (pos? cr)  nil
          (not= 1 d) [[a ar] [bb br]]
          :else      [[aa ar] [b br]])))

(defn solvable? [equations]
  (loop [[a & as :as aa] equations
         [b & bs]        (next equations)
         cs             #{}]
    (cond (nil? a)       cs
          (nil? b)       (recur as (next as) (conj cs a))
          (coprimes a b) (recur aa bs cs)
          :else          (when-let [[aa bb] (simplyfy a b)]
                           (recur (conj (remove #{b} as) bb aa) bs cs)))))

(dotimes [c (first (read-ints))]
  (let [[n]   (read-ints)
        equs  (->> (repeatedly n read-ints)
                   (doall)
                   (map-indexed (fn [i [base remaind]] [base (- (+ i remaind))]))
                   (solvable?))
        time  (if (nil? equs)
                "NEVER"
                (chinese-remainder equs))]
   (printf "Case #%d: %s\n" (inc c) time)))

