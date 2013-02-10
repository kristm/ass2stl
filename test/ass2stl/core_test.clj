(ns ass2stl.core-test
  (:use clojure.test
        ass2stl.core))

(defn sample-file
    []
    (str (System/getProperty "user.dir") "/test/sample.txt"))

(def sample-line
    "Dialogue: 0,0:11:26.40,0:11:27.64,Default,,0,0,0,,Don't you hang the phone")

(def invalid-line
    "Invalid line")

(deftest test-parse-line
    (testing "matches 3 patterns + whole match"
        (is (= 4 (count (parse-line sample-line)))))
    (testing "extract start time"
        (is (= "0:11:26.40" (first (next (parse-line sample-line))))))
        (is (= "0:11:26.40" (nth (parse-line sample-line) 1)))
    (testing "extract end time"
        (is (= "0:11:27.64" (first (next (next (parse-line sample-line)))))))
    (testing "extract dialogue"
        (is (= "Don't you hang the phone" (last (next (parse-line sample-line))))))

    (testing "invalid line"
        (is (= nil (parse-line invalid-line))))

)

(deftest test-convert-msec
    (testing "convert ass timecode to correct stl time value (0 - 25)"
        (is (= 10 (convert-msec 40)))))

(deftest test-convert-ass-timecode
    (testing "convert end portion of time code to its stl equivalent"
        (is (= "0:11:26:22" (convert-ass-timecode "0:11:26.88"))))
    (testing "last portion of time code should be in 2 digit format"
        (is (= "0:11:26:03" (convert-ass-timecode "0:11:26.12")))))

(deftest test-convert-line
    (testing "convert ass line to stl format"
        (is (= "0:11:09:08 , 0:11:10:15 , There you said it!" (convert-line ["Dialogue: 0,0:11:09.33,0:11:10.62,Default,,0,0,0,,There you said it!" "0:11:09.33" "0:11:10.62" "There you said it!"])))))
