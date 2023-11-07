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

(defn set-instrumentation [on?]
  (alter-var-root #'instrument-enable (constantly on?)))

(defn add-instrumentation-only-prefix [p]
  (alter-var-root #'instrument-only-prefixes conj p))

(defn remove-instrumentation-only-prefix [p]
  (alter-var-root #'instrument-only-prefixes (fn [iop] (remove #(= % p) iop))))

(defn add-instrumentation-skip-prefix [p]
  (alter-var-root #'instrument-skip-prefixes conj p))

(defn remove-instrumentation-skip-prefix [p]
  (alter-var-root #'instrument-skip-prefixes (fn [isp] (remove #(= % p) isp))))

(defn skip-instrumentation? [ns-symb]
  (let [nsname (str ns-symb)
        instrument? false
        instrument? (reduce (fn [inst? p]
                              (or inst? (str/starts-with? nsname p)))
                            instrument?
                            instrument-only-prefixes)
        instrument? (reduce (fn [inst? p]
                              (and inst? (not (str/starts-with? nsname p))))
                            instrument?
                            instrument-skip-prefixes)]
    ;; skip iff
    (or (not instrument-enable)
        (not instrument?))))
