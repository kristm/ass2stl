(ns ass2stl.core
    (use clojure.java.io))

(defn read-lines
    [filename]
    (let [rdr (reader filename)]
        (defn read-next-line []
            (if-let [line (.readLine rdr)]
                (cons line(lazy-seq (read-next-line)))
                (.close rdr)))
        (lazy-seq (read-next-line))))

(def re #"((?:\d\d?:){2}\d{2}\.\d{2}),((?:\d\d?:){2}\d{2}\.\d{2}),Default[\d,]*(.*)$")

(defn parse-line
    [line]
    (if-let [line (re-seq re line)] line nil)) ; return line if match else return nil

(defn -main
    [& args]
    (try
        (with-open [rdr (reader (first args))]
            (let [fseq (line-seq rdr)]
                (doseq [line fseq]
                    (when (parse-line line) (println line)))))
        (catch Exception e "System Screamed Error!")))

