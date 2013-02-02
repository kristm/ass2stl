(ns ass2stl.core-test
  (:use clojure.test
        ass2stl.core))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest test-foo
    (testing "return foo"
        (is (= 1 (foo 0)))))
