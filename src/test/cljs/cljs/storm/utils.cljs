(ns cljs.storm.utils
  (:require [cljs.storm.tracer]
            [clojure.string :as str]))

(def captured-traces (atom []))

(defn maybe-serialize [v]
  (if (or (nil? v)    (map? v)    (vector? v)
          (set? v)    (seq? v)    (symbol? v)
          (string? v) (number? v) (boolean? v))
    v

    (-> v
        pr-str
        (str/replace #"#object\[.+\]" "#object[...]"))))

(set! cljs.storm.tracer/trace-fn-call-fn
      (fn [_ form-ns form-name args form-id]
        (swap! captured-traces conj [:fn-call form-ns form-name (into [] (map maybe-serialize) args) form-id])))
(set! cljs.storm.tracer/trace-expr-fn
      (fn [_ val coord form-id]
        (swap! captured-traces conj [:expr-exec (maybe-serialize val) coord form-id])))
(set! cljs.storm.tracer/trace-bind-fn
      (fn [_ coord sym-name val]
        (swap! captured-traces conj [:bind sym-name (maybe-serialize val) coord])))
(set! cljs.storm.tracer/trace-fn-return-fn
      (fn [_ ret-val coord form-id]
        (swap! captured-traces conj [:fn-return (maybe-serialize ret-val) coord form-id])))

(defn reset-captured-traces-fixture [f]
  (reset! captured-traces [])
  (f))

(defn capture [] @captured-traces)
