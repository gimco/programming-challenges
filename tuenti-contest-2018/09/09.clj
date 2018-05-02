#!/usr/bin/env clj
; Scrambled Photo

(require '[clojure.string :as s]
         '[clojure.java.io :as io])
(import '[javax.imageio.ImageIO])

(def img (javax.imageio.ImageIO/read
  (io/input-stream "scrambled-photo-test-bb113a9ce101.png")))

(defn rgb [[x y]] (let [color (.getRGB img x y)]
  (map #(bit-and (bit-shift-right color %) 0xFF) [16 8 0])))

(println (->> (range (.getWidth img))
              (remove #(contains? '#{(255 0 0) (0 255 0)} (rgb [% 0])))
              (mapcat #(for [y (range (.getHeight img))] [% y]))
              (mapcat rgb)
              (map char)
              (s/join)))
