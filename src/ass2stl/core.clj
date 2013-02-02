(ns ass2stl.core
    (:use [clojure.java.io]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn read-lines
    [filename]
    (let [rdr (reader filename)]
        (defn read-next-line []
            (if-let [line (.readLine rdr)]
                (cons line(lazy-seq (read-next-line)))
                (.close rdr)))
        (lazy-seq (read-next-line)))
)

(defn -main
    [& args]
    (doseq [line (read-lines (first args))]
        (println line)))
;    (println (first args)))
