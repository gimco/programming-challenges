#!/usr/bin/env clj
; DNA Splicer

(require '[clojure.string :as s])

(defn suffix [s1 s2]
  (cond
    (s/starts-with? s1 s2) (s/replace-first s1 s2 "")
    (s/starts-with? s2 s1) (s/replace-first s2 s1 "")))

(defrecord State [search selected remain])

(defn try-sample [sample {:keys [search selected remain]}]
  (if-let [newsearch (suffix sample search)]
    (State. newsearch (conj selected sample) (remove #{sample} remain))))

(defn analyze-dna [samples]
  (let [states (map #(State. % #{%} (remove #{%} samples)) samples)]
    (loop [[{:keys [search selected remain] :as state} & xs] states]
      (if (= "" search) selected
          (recur (concat xs (keep #(try-sample % state) remain)))))))

(defn search [line]
  (let [samples  (s/split line #" ")
        selected (analyze-dna samples)
        indexes  (keep-indexed #(when (selected %2) (inc %1)) samples)]
    (s/join "," indexes)))


(def select-mode  #"> Please, provide .*")
(def server-info  #">.*")
(def samples-data #"[\w ]+")

(loop []
  (when-let [line (read-line)]
    (condp re-matches line
      select-mode  (println "SUBMIT")
      server-info  nil
      samples-data (println (search line)))
    (recur)))

; mkfifo p; nc localhost 3241 < p | tee -a 05.log | ./05.clj | tee -a 05.log > p
