(ns cljs.storm.types
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [cljs.storm.tests.types :as ts]
            [cljs.storm.utils :as u]))

(use-fixtures :each u/reset-captured-traces-fixture)

(deftest defrecord-test
  (let [s (ts/->Square 5)
        r (ts/area s)]
    (is (= 25 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests.types" "area" [s] 2018231146]
            [:expr-exec 5 "4,2,1" 2018231146]
            [:expr-exec 5 "4,2,2" 2018231146]
            [:fn-return 25 "4,2" 2018231146]]
           (u/capture)) "captured traces should match.")))

(deftest deftype-test
  (let [r (ts/area (ts/->Circle 2 5))]
    (is (= 12.56 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests.types" "area" ["#object[...]"] -1182932616]
            [:expr-exec 2 "4,2,2" -1182932616]
            [:expr-exec 2 "4,2,3" -1182932616]
            [:fn-return 12.56 "4,2" -1182932616]]
           (u/capture)) "captured traces should match.")))

(deftest extend-type-basic-test
  (let [r (ts/area "tha-shape")]
    (is (= 9 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests.types" "area" ["tha-shape"] 1515835378]
            [:bind "s" "tha-shape" "3"]
            [:expr-exec "tha-shape" "3,2,1" 1515835378]
            [:fn-return 9 "3,2" 1515835378]]
           (u/capture)) "captured traces should match.")))

(deftest extend-type-proto-test
  (let [tr (ts/->Triangle 2 5)
        r (ts/sides-count tr)]
    (is (= 3 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests.types" "sides-count" [tr] 1211097684]
            [:expr-exec 2 "3,2,2" 1211097684]
            [:fn-return 3 "3,2" 1211097684]]
           (u/capture)) "captured traces should match.")))

(deftest extend-proto-basic-test
  (let [r (ts/area 10)]
    (is (= 100 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests.types" "area" [10] 1752788479]
            [:bind "n" 10 ""]
            [:expr-exec 10 "8,2,1" 1752788479]
            [:expr-exec 10 "8,2,2" 1752788479]
            [:fn-return 100 "8,2" 1752788479]]
           (u/capture)) "captured traces should match.")))

(deftest extend-proto-type-test
  (let [rect (ts/->Rectangle 2 4)
        r (ts/area rect)]
    (is (= 8 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests.types" "area" [rect] 1752788479]
            [:bind "r" rect "3"]
            [:bind "r" rect ""] ;; TODO: hmm why is this?
            [:expr-exec rect "3,2,1,1" 1752788479]
            [:expr-exec 2 "3,2,1" 1752788479]
            [:expr-exec rect "3,2,2,1" 1752788479]
            [:expr-exec 4 "3,2,2" 1752788479]
            [:fn-return 8 "3,2" 1752788479]]
           (u/capture)) "captured traces should match.")))
