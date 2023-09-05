(ns cljs.storm.emitter
  (:require [clojure.string :as str]))

#?(:clj (def instrument-enable
          (some-> (System/getProperty "cljs.storm.instrumentEnable")
                  Boolean/parseBoolean)))

#?(:clj (def instrument-only-prefixes
          (some-> (System/getProperty "cljs.storm.instrumentOnlyPrefixes")
                  (.split ","))))

#?(:clj (def instrument-skip-prefixes
          (some-> (System/getProperty "cljs.storm.instrumentSkipPrefixes")
                  (.split ","))))

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
        prefixes-instrument (if (seq instrument-only-prefixes)
                              ;; if there are instrument onlys, lets see if any
                              ;; of them applies for this fq-fn-name
                              (some (fn [p]
                                      (str/starts-with? nsname p))
                                    instrument-only-prefixes)

                              ;; else, if there are skips lets see if we shouldn't skip
                              ;; this fq-fn-name                            
                              (not
                               (some (fn [p]
                                       (str/starts-with? nsname p))
                                     instrument-skip-prefixes)))]
    ;; skip iff
    (not (and instrument-enable
              prefixes-instrument))))
