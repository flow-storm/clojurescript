(ns cljs.storm.api
  (:require [cljs.storm.emitter :as emitter]
            [cljs.storm.utils :as storm-utils]))

(defn add-instr-prefix

  "Add instrumentation `prefix`.
  When `touch-path` is provided, touches all the cljs and cljc files recusively.
  Useful for making watchers like the shadow-cljs one recompile and reload files."

  ([prefix] (add-instr-prefix prefix nil))
  ([prefix touch-path]
   (emitter/add-instrumentation-only-prefix prefix)
   (when touch-path
     (storm-utils/touch-cljs-files touch-path))))

(defn rm-instr-prefix

  "Remove instrumentation `prefix`.
  When `touch-path` is provided, touches all the cljs and cljc files recusively.
  Useful for making watchers like the shadow-cljs one recompile and reload files."

  ([prefix] (add-instr-prefix prefix nil))
  ([prefix touch-path]
   (emitter/remove-instrumentation-only-prefix prefix)
   (when touch-path
     (storm-utils/touch-cljs-files touch-path))))

(defn get-instr-prefixes []
  emitter/instrument-only-prefixes)
