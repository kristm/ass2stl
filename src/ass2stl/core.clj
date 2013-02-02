(ns ass2stl.core
    (:use [clojure.java.io]))

(defn foo
    [x]
    x)

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
