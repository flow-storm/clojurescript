(ns storm-test-runner
  (:require [cljs.test :refer-macros [run-tests]]
            [cljs.storm.functions-test]
            [cljs.storm.bodies-test]
            [cljs.storm.types-test]))

(enable-console-print!)

(run-tests
 'cljs.storm.functions-test
 'cljs.storm.bodies-test
 'cljs.storm.types-test
  )
