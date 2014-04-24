(ns ass2stl.core-test
  (:use clojure.test
        ass2stl.core))

(defn sample-file
    []
    (str (System/getProperty "user.dir") "/test/sample.txt"))

(def sample-line
    "Dialogue: 0,0:11:26.40,0:11:27.64,Default,,0,0,0,,Don't you hang the phone")

(def verbose-line
    "Dialogue: 0,0:00:28.00,0:00:36.89,Default,,0,0,0,,{\\move(427,470,427,470,28,-14)}Hello Good Afternoon, Is Happy Around?")

(def formatted-line
    "Dialogue: 0,0:00:28.00,0:00:36.89,Default,,0,0,0,,{\\i1}It isn't real if it isn't true")

(def formatted-line-2
    "Si, una \"{\\i1}cita{\\i0}.\"")

(def spanish-line
    "Dialogue: 0,0:06:39.00,0:06:42.37,Default,,0,0,0,,¿Qué estás haciendo ahora?")

;(def line-with-quote
;    "Dialogue: 0,0:06:07.82,0:06:09.40,Default,,0,0,0,,As if everything is "OK""
;)

(def invalid-line
    "Invalid line")

(deftest test-parse-line
    (testing "matches 3 patterns + whole match + optional subtitle formatting"
        (is (= 5 (count (parse-line sample-line)))))
    (testing "extract start time"
        (is (= "0:11:26.40" (first (next (parse-line sample-line))))))
        (is (= "0:11:26.40" (nth (parse-line sample-line) 1)))
    (testing "extract end time"
        (is (= "0:11:27.64" (first (next (next (parse-line sample-line)))))))
    (testing "extract dialogue"
        (is (= "Don't you hang the phone" (last (next (parse-line sample-line))))))
    (testing "get empty subtitle format if not given"
        (is (nil? (nth (parse-line sample-line) 3))))

    (testing "complex line"
        (is (= "Hello Good Afternoon, Is Happy Around?" (last (parse-line verbose-line)))))
    (testing "get subtitle format instruction"
        (not (= nil (nth (parse-line verbose-line) 3))))

    (testing "invalid line"
        (is (= nil (parse-line invalid-line))))
)

(deftest test-convert-msec
    (testing "convert ass timecode to correct stl time value (0 - 25)"
        (is (= 10 (convert-msec 40)))))

(deftest test-convert-ass-timecode
    (testing "convert end portion of time code to its stl equivalent"
        (is (= "00:11:26:22" (convert-ass-timecode "0:11:26.88"))))
    (testing "last portion of time code should be in 2 digit format"
        (is (= "00:11:26:03" (convert-ass-timecode "0:11:26.12")))))

(deftest test-convert-to-fcpxml-time
    (testing "convert aegisub time format to fcpxml"
        (is (= "1642080/24000s" (convert-to-fcpxml-time "0:01:08.42")))))

(deftest test-convert-to-seconds
    (testing "convert timecode values to seconds"
        (is (= 672000 (convert-to-seconds "0:00:28.0")))))

(deftest test-convert-to-seconds
    (testing "convert timecode values to seconds (2)"
        (is (= 885360 (convert-to-seconds "0:00:36.89")))))

(deftest test-fcpxml-duration
    (testing "compute duration based off start time and end time minute values"
        (is (= "1066800/120000s" (fcpxml-duration 672000 885360)))))

(deftest test-convert-fcpxml
    (testing "convert aegisub format to fcpx xml"
        (is (= "<title lane=\"1\" offset=\"-337646/24000s\" ref=\"r11\" name=\"TextUp Regular: It isn't real if it isn't true\" duration=\"1066800/120000s\" start=\"86486400/24000s\" role=\"subtitle\">\n\t<param name=\"Position\" key=\"9999/16130/16136/1/100/101\" value=\"0 -382\"/>\n\t<param name=\"Anchor Point\" key=\"9999/16130/16136/1/100/107\" value=\"768 50\"/>\n\t<text>\n\t\t<text-style ref=\"ts12\">It isn't real if it isn't true</text-style>\n\t</text>\n</title>" (convert-fcpxml (parse-line formatted-line)))))
    (testing "sample"
      (println spanish-line)
      (println (apply str [">> " (parse-line spanish-line)]))
    )
)

(deftest test-strip-format
  (testing "remove formatting characters from string"
    (is (= "Si, una \"cita.\"", (strip-format formatted-line-2)))
  )
)


(deftest test-convert-line
    (testing "convert ass line to stl format"
        (is (= "00:11:09:08 , 00:11:10:15 , There you said it!" (convert-line ["Dialogue: 0,0:11:09.33,0:11:10.62,Default,,0,0,0,,There you said it!" "0:11:09.33" "0:11:10.62" "There you said it!"]))))
    (testing "convert verbose line"
        (is (= "00:00:28:00 , 00:00:36:22 , Hello Good Afternoon, Is Happy Around?" (convert-line (parse-line verbose-line)))))
    (testing "convert formatted line"
        (is (= "00:00:28:00 , 00:00:36:22 , ^IIt isn't real if it isn't true^I" (convert-line (parse-line formatted-line)))))
)
