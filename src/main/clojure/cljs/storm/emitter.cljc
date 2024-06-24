(ns cljs.storm.emitter
  (:require [clojure.string :as str]))

#?(:clj (def instrument-enable
          (some-> (System/getProperty "cljs.storm.instrumentEnable")
                  Boolean/parseBoolean))
   :cljs (def instrument-enable nil))

#?(:clj (def instrument-only-prefixes
          (some-> (System/getProperty "cljs.storm.instrumentOnlyPrefixes")
                  (str/split #",")))
   :cljs (def instrument-only-prefixes nil))

#?(:clj (def instrument-skip-prefixes
          (some-> (System/getProperty "cljs.storm.instrumentSkipPrefixes")
                  (str/split #",")))
   :cljs (def instrument-skip-prefixes nil))

#?(:clj (def instrument-skip-regex
          (when-let [re (System/getProperty "cljs.storm.instrumentSkipRegex")]
            (re-pattern re)))
   :cljs (def instrument-skip-regex nil))

#?(:clj (defn set-instrumentation [on?]
          (alter-var-root #'instrument-enable (constantly on?)))
   :cljs (defn set-instrumentation [_] (js/console.error "Not implemented")))

#?(:clj (defn add-instrumentation-only-prefix [p]
          (alter-var-root #'instrument-only-prefixes conj p))
   :cljs (defn add-instrumentation-only-prefix [_] (js/console.error "Not implemented")))

#?(:clj (defn remove-instrumentation-only-prefix [p]
          (alter-var-root #'instrument-only-prefixes (fn [iop] (remove #(= % p) iop))))
   :cljs (defn remove-instrumentation-only-prefix [_] (js/console.error "Not implemented")))

#?(:clj (defn add-instrumentation-skip-prefix [p]
          (alter-var-root #'instrument-skip-prefixes conj p))
   :cljs (defn add-instrumentation-skip-prefix [_] (js/console.error "Not implemented")))

#?(:clj (defn remove-instrumentation-skip-prefix [p]
          (alter-var-root #'instrument-skip-prefixes (fn [isp] (remove #(= % p) isp))))
   :cljs (defn remove-instrumentation-skip-prefix [_] (js/console.error "Not implemented")))

#?(:clj (defn set-instrumentation-skip-regex [re]
          (alter-var-root #'instrument-skip-regex (constantly (re-pattern re))))
   :cljs (defn set-instrumentation-skip-regex [_] (js/console.error "Not implemented")))

#?(:clj (defn remove-instrumentation-skip-regex []
          (alter-var-root #'instrument-skip-regex (constantly nil)))
   
   :cljs (defn remove-instrumentation-skip-regex []) (js/console.error "Not implemented"))

#?(:clj (defn skip-instrumentation? [ns-symb]
          (let [nsname (str ns-symb)
                instrument? false
                instrument? (reduce (fn [inst? p]
                                      (or inst? (str/starts-with? nsname p)))
                                    instrument?
                                    instrument-only-prefixes)
                instrument? (reduce (fn [inst? p]
                                      (and inst? (not (str/starts-with? nsname p))))
                                    instrument?
                                    instrument-skip-prefixes)
                instrument? (if-not instrument-skip-regex
                              instrument?
                              (and instrument? (not (re-find instrument-skip-regex nsname))))]
            ;; skip iff
            (or (not instrument-enable)
                (not instrument?))))
   :cljs (defn skip-instrumentation? [_] (js/console.error "Not implemented")))
