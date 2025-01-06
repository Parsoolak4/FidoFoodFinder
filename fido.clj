(require '[clojure.string :as str])

;; Food finding functions
(def find-food
  (let [string->map (fn [content]
                      (mapv vec (str/split-lines content)))
        
        validate-map (fn [content]
                      (let [lines (str/split-lines content)
                            width (count (first lines))]
                        (and (> (count lines) 0)
                             (every? #(= width (count %)) lines))))
        
        get-pos (fn [map-data [x y]]
                 (get-in map-data [y x]))
        
        is-food? (fn [map-data pos]
                  (= (get-pos map-data pos) \@))
        
        can-move? (fn [map-data [x y]]
                   (when (and (>= x 0) (>= y 0)
                             (< y (count map-data))
                             (< x (count (first map-data))))
                     (let [val (get-pos map-data [x y])]
                       (or (= val \-) (= val \@)))))
        
        find-path (fn find-path [map-data pos visited]
                   (if (is-food? map-data pos)
                     [true [pos] visited]
                     (let [moves (for [[dx dy] [[1 0] [0 1] [-1 0] [0 -1]]
                                     :let [new-pos [(+ (pos 0) dx) (+ (pos 1) dy)]]
                                     :when (and (can-move? map-data new-pos)
                                              (not (contains? visited new-pos)))]
                                 new-pos)]
                       (loop [[move & rest-moves] moves
                             all-visited (conj visited pos)]
                         (if-not move
                           [false [] all-visited]
                           (let [[found? path new-visited] (find-path map-data move all-visited)]
                             (if found?
                               [true (cons pos path) new-visited]
                               (recur rest-moves new-visited))))))))
        
        mark-map (fn [map-data path visited]
                  (reduce (fn [acc [y x]]
                           (let [current (get-in acc [y x])
                                 pos [x y]]
                             (assoc-in acc [y x]
                                      (cond
                                        (= current \@) \@
                                        (= current \#) \#
                                        (contains? (set path) pos) \+
                                        (contains? visited pos) \!
                                        :else \-))))
                         map-data
                         (for [y (range (count map-data))
                               x (range (count (first map-data)))]
                           [y x])))
        
        map->string (fn [map-data]
                     (str/join "\n" (map str/join map-data)))]
    
    (fn [content]
      (let [map-data (string->map content)]
        (when (validate-map content)
          (let [[found? path visited] (find-path map-data [0 0] #{})]
            [found? (mark-map map-data path visited)]))))))

;; Menu functions
(defn clear-screen []
  (print (str (char 27) "[2J"))
  (flush))

(defn show-menu []
  (println "*** Let's Feed Fido ***")
  (println "----------------------")
  (println "1. Display list of map files")
  (println "2. Display a map for Fido")
  (println "3. Exit")
  (print "Enter an option? ")
  (flush))

(defn get-map-files []
  (filter #(re-matches #".*\.txt$" (.getName %))
          (filter #(.isFile %) (file-seq (clojure.java.io/file ".")))))

(defn display-maps []
  (println "\nMap List:")
  (doseq [file (get-map-files)]
    (println (str "* ./" (.getName file))))
  (println "\nPress any key to continue")
  (read-line)
  (clear-screen))

(defn process-map []
  (print "\nPlease enter a file name => ")
  (flush)
  (let [filename (read-line)]
    (if (.exists (clojure.java.io/file filename))
      (let [content (slurp filename)]
        (println "\nThis is Fido's challenge:")
        (println content)
        (if-let [[found? result] (find-food content)]
          (do
            (if found?
              (println "Woo Hoo - Fido found her food")
              (println "Oh no - Fido could not find her food"))
            (println (str/join "\n" (map str/join result))))
          (println "Unfortunately, this is not a valid food map for Fido")))
      (println "Oops: specified file" filename "does not exist"))
    (println "\nPress any key to continue")
    (read-line)
    (clear-screen)))

;; Main program loop
(clear-screen)
(loop []
  (show-menu)
  (let [choice (read-line)]
    (clear-screen)
    (case choice
      "1" (do (display-maps)
              (recur))
      "2" (do (process-map)
              (recur))
      "3" (println "Good Bye")
      (do (println "Invalid option!")
          (Thread/sleep 1000)
          (clear-screen)
          (recur)))))