(ns cljs.storm.functions-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [cljs.storm.utils :as u]
            [cljs.storm.tests-code.functions :as fns]))

(use-fixtures :each u/reset-captured-traces-fixture)

(deftest empty-body-fn-test
  (let [r (fns/empty-body 40 2)]
    (is (= nil r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests-code.functions" "empty-body" [40 2] 110441699]
            [:bind "a" 40 ""]
            [:bind "b" 2 ""]
            [:fn-return nil ""]]
           (u/capture)) "captured traces should match.")))

(deftest simple-fn-test
  (let [r (fns/simple 40 2)]
    (is (= 42 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests-code.functions" "simple" [40 2] 1954697464]
            [:bind "a" 40 ""]
            [:bind "b" 2 ""]
            [:expr-exec 40 "3,1"]
            [:expr-exec 2 "3,2"]
            [:fn-return 42 "3"]]
           (u/capture)) "captured traces should match.")))

(deftest defed-fn-test
  (let [r (fns/defed)]
    (is (= 42 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests-code.functions" "defed" [] -699754393]
            [:fn-return 42 "2"]]
           (u/capture)) "captured traces should match.")))

(deftest multi-arity-fn-test
  (let [r (fns/multi-arity 40)]
    (is (= 42 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests-code.functions" "multi-arity" [40] -876490731]
            [:bind "a" 40 ""]
            [:expr-exec 40 "2,1,1"]
            [:fn-call "cljs.storm.tests-code.functions" "multi-arity" [40 2] -876490731]
            [:bind "a" 40 ""]
            [:bind "b" 2 ""]
            [:expr-exec 40 "3,1,1"]
            [:expr-exec 2 "3,1,2"]
            [:fn-return 42 "3,1"]
            [:fn-return 42 "2,1"]]
           (u/capture)) "captured traces should match.")))

(deftest args-destructuring-fn-test
  (let [r (fns/args-destructuring {:a 40 :b 2})]
    (is (= 42 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests-code.functions" "args-destructuring" [{:a 40, :b 2}] 713448837]
            [:bind "a" 40 ""]
            [:bind "b" 2 ""]
            [:expr-exec 40 "3,1"]
            [:expr-exec 2 "3,2"]
            [:fn-return 42 "3"]]
           (u/capture)) "captured traces should match.")))

(deftest variadic-fn-test
  (let [r (fns/variadic 1 2 3 4)]
    (is (= 10 r) "function return should be right.")
    (is (= '[[:fn-call "cljs.storm.tests-code.functions" "variadic" [(1 2 3 4)] 1269596501]
             [:bind "nums" (1 2 3 4) ""]
             [:expr-exec (1 2 3 4) "3,2"]
             [:fn-return 10 "3"]]
           (u/capture)) "captured traces should match.")))

(deftest tail-recursive-fn-test
  (let [r (fns/tail-recursive 0 2)]
    (is (= 3 r) "function return should be right.")
    (is (= [[:fn-call "cljs.storm.tests-code.functions" "tail-recursive" [0 2] 1815279187]
            [:bind "s" 0 ""]
            [:bind "n" 2 ""]
            [:expr-exec 2 "3,1,1"]
            [:expr-exec false "3,1"]
            [:expr-exec 0 "3,3,1,1"]
            [:expr-exec 2 "3,3,1,2"]
            [:expr-exec 2 "3,3,1"]
            [:expr-exec 2 "3,3,2,1"]
            [:expr-exec 1 "3,3,2"]
            [:expr-exec 1 "3,1,1"]
            [:expr-exec false "3,1"]
            [:expr-exec 2 "3,3,1,1"]
            [:expr-exec 1 "3,3,1,2"]
            [:expr-exec 3 "3,3,1"]
            [:expr-exec 1 "3,3,2,1"]
            [:expr-exec 0 "3,3,2"]
            [:expr-exec 0 "3,1,1"]
            [:expr-exec true "3,1"]
            [:fn-return 3 "3,2"]]
           (u/capture)) "captured traces should match.")))

(deftest recursive-fn-test
  (let [r (fns/factorial 4)]
    (is (= 24 r) "function return should be right.")
    (is (=  [[:fn-call "cljs.storm.tests-code.functions" "factorial" [4] 71712880]
             [:bind "n" 4 ""]
             [:expr-exec 4 "3,1,1"]
             [:expr-exec false "3,1"]
             [:expr-exec 4 "3,3,1"]
             [:expr-exec 4 "3,3,2,1,1"]
             [:expr-exec 3 "3,3,2,1"]
             [:fn-call "cljs.storm.tests-code.functions" "factorial" [3] 71712880]
             [:bind "n" 3 ""]
             [:expr-exec 3 "3,1,1"]
             [:expr-exec false "3,1"]
             [:expr-exec 3 "3,3,1"]
             [:expr-exec 3 "3,3,2,1,1"]
             [:expr-exec 2 "3,3,2,1"]
             [:fn-call "cljs.storm.tests-code.functions" "factorial" [2] 71712880]
             [:bind "n" 2 ""]
             [:expr-exec 2 "3,1,1"]
             [:expr-exec false "3,1"]
             [:expr-exec 2 "3,3,1"]
             [:expr-exec 2 "3,3,2,1,1"]
             [:expr-exec 1 "3,3,2,1"]
             [:fn-call "cljs.storm.tests-code.functions" "factorial" [1] 71712880]
             [:bind "n" 1 ""]
             [:expr-exec 1 "3,1,1"]
             [:expr-exec false "3,1"]
             [:expr-exec 1 "3,3,1"]
             [:expr-exec 1 "3,3,2,1,1"]
             [:expr-exec 0 "3,3,2,1"]
             [:fn-call "cljs.storm.tests-code.functions" "factorial" [0] 71712880]
             [:bind "n" 0 ""]
             [:expr-exec 0 "3,1,1"]
             [:expr-exec true "3,1"]
             [:fn-return 1 ""]
             [:expr-exec 1 "3,3,2"]
             [:fn-return 1 "3,3"]
             [:expr-exec 1 "3,3,2"]
             [:fn-return 2 "3,3"]
             [:expr-exec 2 "3,3,2"]
             [:fn-return 6 "3,3"]
             [:expr-exec 6 "3,3,2"]
             [:fn-return 24 "3,3"]]
           (u/capture)) "captured traces should match.")))

(deftest multimethods-test
  (let [r (fns/area {:type :square :side 4})]
    (is (= 16 r) "function return should be right.")
    (is (=  [[:fn-call "cljs.storm.tests-code.functions" "area" [{:type :square, :side 4}] -1257757404]
             [:bind "side" 4 ""]
             [:expr-exec 4 "4,1"]
             [:expr-exec 4 "4,2"]
             [:fn-return 16 "4"]]
           (u/capture)) "captured traces should match.")))
