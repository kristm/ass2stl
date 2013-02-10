(ns ass2stl.core
    (use clojure.java.io))

(def stl-header {
    :FontName "Arial"
    :FontSize "28"
    :Bold "False"
    :Italic "False"
    :Underlined "False"
    :TextContrast "15"
    :Outline1Contrast  "13"
    :Outline2Contrast  "0"
    :HorzAlign  "center"
    :VertAlign  "bottom"
    :XOffset  "0"
    :YOffset  "0"
    :FadeIn  "1"
    :FadeOut  "0"
    :BackgroundContrast "0"
    :ForceDisplay "false"
    :TapeOffset "false"
    })

(def re #"((?:\d\d?:){2}\d{2}\.\d{2}),((?:\d\d?:){2}\d{2}\.\d{2}),Default[\d,]*(.*)$")

(defn print-header
    []
    (for [[k v] stl-header]
        (apply str ["$" (subs (str k) 1) " = " v] )))

(defn write-header
    [output]
    (binding [*out* (java.io.FileWriter. output)]
        (doseq [line (print-header)]
            (println line))))

(defn parse-line
    [line]
    (if-let [line (re-seq re line)] (first line) nil)) ; return line if match else return nil

(defn convert-msec
    [ass_sec]
    (int (/ ass_sec 3.92)))

(defn -main
    [& args]
    (try
        (write-header (clojure.string/replace (first args) #"(.*)\.[a-zA-Z]*$" "$1.stl"))
        (println (class (first args)))
        (with-open [rdr (reader (first args))]
            (let [fseq (line-seq rdr)]
                (doseq [line fseq]
                    (when (parse-line line) 
                        (println line)))))
        (catch Exception e "System Screamed Error!")))

