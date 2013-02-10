(ns ass2stl.core-test
  (:use clojure.test
        ass2stl.core))

(defn sample-file
    []
    (str (System/getProperty "user.dir") "/test/sample.txt"))

(deftest test-read-lines
    (testing "read line returns a lazy sequence of lines"
            (is (= clojure.lang.LazySeq (class (read-lines (sample-file)))))))

(def sample-line
    "Dialogue: 0,0:11:26.40,0:11:27.64,Default,,0,0,0,,Don't you hang the phone")

(def invalid-line
    "Invalid line")

(deftest test-parse-line
    (testing "matches 3 patterns + whole match"
        (is (= 4 (count (first (parse-line sample-line))))))
    (testing "extract start time"
        (is (= "0:11:26.40" (first (next (first (parse-line sample-line)))))))
    (testing "extract end time"
        (is (= "0:11:27.64" (first (next (next (first (parse-line sample-line))))))))
    (testing "extract dialogue"
        (is (= "Don't you hang the phone" (last (next (first (parse-line sample-line)))))))

    (testing "invalid line"
        (is (= nil (parse-line invalid-line))))

)
