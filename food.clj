(ns food
  (:require [clojure.string :as str]))

(defn string->map [content]
  (mapv vec (str/split-lines content)))

(defn validate-map [content]
  (let [lines (str/split-lines content)
        width (count (first lines))]
    (and (> (count lines) 0)
         (every? #(= width (count %)) lines))))

(defn in-bounds? [map-data [x y]]
  (and (>= x 0) (>= y 0)
       (< y (count map-data))
       (< x (count (first map-data)))))

(defn get-pos [map-data [x y]]
  (when (in-bounds? map-data [x y])
    (get-in map-data [y x])))

(defn can-move? [map-data [x y]]
  (let [val (get-pos map-data [x y])]
    (and val (not= val \#))))

(defn is-food? [map-data pos]
  (= (get-pos map-data pos) \@))

(defn get-neighbors [[x y]]
  [[x (dec y)]  ; up
   [(inc x) y]  ; right
   [x (inc y)]  ; down
   [(dec x) y]]) ; left

(defn find-path [map-data start]
  (loop [queue (conj clojure.lang.PersistentQueue/EMPTY start)
         visited {start nil}  ; map of position to its predecessor
         current start]
    (if (empty? queue)
      [false [] visited]
      (let [current (peek queue)
            neighbors (get-neighbors current)
            valid-neighbors (filter #(and (can-move? map-data %)
                                        (not (contains? visited %)))
                                  neighbors)]
        (if (is-food? map-data current)
          (let [path (loop [pos current
                           path []]
                      (if pos
                        (recur (get visited pos) (conj path pos))
                        path))]
            [true (reverse path) visited])
          (let [new-queue (into (pop queue) valid-neighbors)
                new-visited (reduce #(assoc %1 %2 current) 
                                  visited 
                                  valid-neighbors)]
            (recur new-queue new-visited current)))))))

(defn mark-path [map-data path visited]
  (let [visited-set (set visited)]  ; Convert visited sequence to a set
    (reduce (fn [acc [y x]]
              (let [current (get-in acc [y x])
                    in-path? (some #{[x y]} path)
                    new-char (cond
                             (= current \@) \@
                             (= current \#) \#
                             in-path? \+
                             (contains? visited-set [x y]) \!
                             :else \-)]
                (assoc-in acc [y x] new-char)))
            map-data
            (for [y (range (count map-data))
                  x (range (count (first map-data)))]
              [y x]))))

(defn find-food [content]
  (let [map-data (string->map content)
        [found? path visited] (find-path map-data [0 0])]
    [found? (mark-path map-data path (keys visited))]))

(defn map->string [map-data]
  (str/join "\n" (map str/join map-data)))