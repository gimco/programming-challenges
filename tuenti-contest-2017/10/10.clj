#!/usr/bin/env clj
; Passwords

(require '[clojure.string :as s])

(defn read-tuple []
  (let [[a b] (s/split (read-line) #" ")]
    (vector a (read-string b))))

(defn crc32 [x]
  (let [crc (new java.util.zip.CRC32)]
    (. crc update (. x getBytes))
    (BigInteger/valueOf (. crc getValue))))

(defn md5 [x]
  (let [md5 (java.security.MessageDigest/getInstance "MD5")
        md5sum (. md5 digest (. x getBytes))]
    (->> (new BigInteger 1 md5sum)
         (format "%032X")
         (s/lower-case))))

(defn modpow-precalc [& secrets]
  (let [[secret1 secret2] (mapv biginteger secrets)
        modpow            (.modPow secret1 (biginteger 1e7) secret2)]
    [secret1 secret2 modpow]))

(def secrets-map
  (->> (slurp "secrets.txt")
       (s/split-lines)
       (map #(s/split % #" "))
       (reduce
         (fn [m [k secret1 secret2]]
           (assoc m k (modpow-precalc secret1 secret2)))
         {})))

(defn chr [c] (char (+ 33 (mod c 94))))

(defn generate-password [date hash]
  (let [[secret1 secret2 modpow] (secrets-map date)
        calc                     #(mod (* % secret1) secret2)
        next-char                (fn [[password counter] _] [(str password (chr counter)) (calc counter)])
        counter                  (mod (* modpow (crc32 hash)) secret2)
        [password _]             (reduce next-char ["" (calc counter)] (range 10))]
    [password (md5 password)]))

(defn generate-password-date [[_ hash] [date n]]
  (loop [n n
         [password hash] ["" hash]]
    (if (zero? n) [password hash]
        (recur (dec n) (generate-password date hash)))))

(dotimes [t (read-string (read-line))]
  (let [[userid n]   (read-tuple)
        [password _] (->> (repeatedly n read-tuple)
                          (reduce generate-password-date ["" userid]))]
    (flush)
    (printf "Case #%d: %s\n" (inc t) password)))
