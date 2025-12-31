(require '[cljs.closure :as cljsc])
(require '[cljs.storm.emitter :as storm-emitter])

(storm-emitter/set-instrumentation false)
(cljsc/aot-cache-core)
