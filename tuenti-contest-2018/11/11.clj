#!/usr/bin/env clj
; Lasers








(dotimes [c (first (read-ints))]
  (let [[n m i] (read-ints)
        items   (->> (repeatedly i read-ints)
                     (doall))]
   (printf "Case #%d: %s %s\n" (inc c) 0)))











4

3 4 4
0 1
1 2
2 0
2 3

3 3 0

3 3 9
0 0
0 1
0 2
1 0
1 1
1 2
2 0
2 1
2 2


4 4 6
0 0
0 1
1 2
1 3
2 1
3 0
