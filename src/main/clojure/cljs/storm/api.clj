(ns cljs.storm.api
  (:require [cljs.storm.emitter :as emitter]
            [cljs.storm.utils :as storm-utils]))

(defn add-instr-only-prefix

  "Add instrumentation `prefix`.
  When `touch-path` is provided, touches all the cljs and cljc files recusively.
  Useful for making watchers like the shadow-cljs one recompile and reload files."

  ([prefix] (add-instr-only-prefix prefix nil))
  ([prefix touch-path]
   (emitter/add-instrumentation-only-prefix prefix)
   (when touch-path
     (storm-utils/touch-cljs-files touch-path))))

(defn rm-instr-only-prefix

  "Remove instrumentation `prefix`.
  When `touch-path` is provided, touches all the cljs and cljc files recusively.
  Useful for making watchers like the shadow-cljs one recompile and reload files."

  ([prefix] (rm-instr-only-prefix prefix nil))
  ([prefix touch-path]
   (emitter/remove-instrumentation-only-prefix prefix)
   (when touch-path
     (storm-utils/touch-cljs-files touch-path))))

(defn add-instr-skip-prefix

  "Add instrumentation `prefix`.
  When `touch-path` is provided, touches all the cljs and cljc files recusively.
  Useful for making watchers like the shadow-cljs one recompile and reload files."

  ([prefix] (add-instr-skip-prefix prefix nil))
  ([prefix touch-path]
   (emitter/add-instrumentation-skip-prefix prefix)
   (when touch-path
     (storm-utils/touch-cljs-files touch-path))))

(defn rm-instr-skip-prefix

  "Remove instrumentation `prefix`.
  When `touch-path` is provided, touches all the cljs and cljc files recusively.
  Useful for making watchers like the shadow-cljs one recompile and reload files."

  ([prefix] (rm-instr-skip-prefix prefix nil))
  ([prefix touch-path]
   (emitter/remove-instrumentation-skip-prefix prefix)
   (when touch-path
     (storm-utils/touch-cljs-files touch-path))))

(defn set-instr-skip-regex [re]
  (emitter/set-instrumentation-skip-regex re))

(defn rm-instr-skip-regex []
  (emitter/remove-instrumentation-skip-regex))

(defn get-instr-prefixes []
  emitter/instrument-only-prefixes)

(defn get-skip-prefixes []
  emitter/instrument-skip-prefixes)

(defn get-skip-regex []
  emitter/instrument-skip-regex)

(defn set-instrumentation [on?]
  (emitter/set-instrumentation on?))
