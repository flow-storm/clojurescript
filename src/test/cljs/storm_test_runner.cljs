(ns storm-test-runner
  (:require [cljs.test :refer-macros [run-tests]]
            [cljs.storm.functions]
            [cljs.storm.bodies]
            [cljs.storm.types]))

(enable-console-print!)

(run-tests
 'cljs.storm.functions
 'cljs.storm.bodies
 'cljs.storm.types
  )
