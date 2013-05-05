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

(def stl-format {
    :i1 "^I"})

(def re #"((?:\d\d?:){2}\d{2}\.\d{2}),((?:\d\d?:){2}\d{2}\.\d{2}),Default[\d\,]+(?:\{\\?([a-z\d\\]*(?:\([0-9\,\-]*\))?)?\})?([^\{\}]*)$")

(defn print-header
    []
    (for [[k v] stl-header]
        (apply str ["$" (subs (str k) 1) " = " v] )))

(defn write-header
    [output]
    (binding [*out* (java.io.FileWriter. output)]
        (doseq [line (print-header)] (println line))))

(defn parse-line
    [line]
    (if-let [line (re-seq re line)] (first line) nil)) ; return line if match else return nil

(defn convert-to-fcpxml-time
    [ass-time]
    (when-let [timematch (first (re-seq #"^(\d+)\:(\d+)\:(\d+)\.(\d+)$" ass-time))]
        (let [hour (nth timematch 1) minute (nth timematch 2) sec (nth timematch 3) endf (nth timematch 4)]
            ;(println (apply str [hour "|" minute "|" sec "|" endf])))))
            (apply str [(Math/round (* (Float. (apply str [ (+ (* (Integer. minute) 60) (Integer. sec)) "." endf ])) 24000)) "/24000s"]))))

(defn convert-msec
    [ass_sec]
    (int (/ ass_sec 3.92)))

(defn convert-ass-timecode
    [ass-time]
    (when-let [timematch (first (re-seq #"^(\d+)([\d\:]+)\.(\d+)$" ass-time))]
        (let [hour (nth timematch 1) minute (nth timematch 2) endf (nth timematch 3)]
            (apply str [ (format "%02d" (Integer. hour)) minute ":" (format "%02d" (convert-msec (Integer. endf)))]))))

(defn convert-dialogue
    [line]
    (defn get-stl-format [key] (get stl-format (keyword key)))
    (if-let [formatter (nth line 3)] 
        (apply str [(get-stl-format formatter) (last line) (get-stl-format formatter)]) 
        (last line)))

(defn convert-line
    [line]
    (apply str [(convert-ass-timecode (nth line 1)) " , " (convert-ass-timecode (nth line 2)) " , " (convert-dialogue line)]))

(defn strip-format
    [line]
    (last line))

(defn convert-fcpxml
    [line]
    (apply str ["<title lane='1' offset='" (convert-to-fcpxml-time (nth line 1)) "' ref='r11' name='TextUp Bold: " (strip-format line) "' duration='xxx/120000s' start='86486400/24000s' role='subtitle'><text>" (strip-format line) "</text></title>"])
)

(defn -main
    [& args]
    (def output (clojure.string/replace (first args) #"(.*)\.[a-zA-Z]*$" "$1.stl"))
    (try
        (write-header output)
        (with-open [rdr (reader (first args))]
            (let [fseq (line-seq rdr)]
                (doseq [line fseq]
                    (binding [*out* (java.io.FileWriter. output, true)]
                        (when-let [matched-line (parse-line line)] 
                            (println (convert-line matched-line)))))))
        (println (apply str ["STL file written: " output]))
        (catch Exception e "System Screamed Error!")))
