(ns cljs.storm.utils
  (:require [clojure.string :as str]
            [clojure.java.io :as io])
  #?(:clj (:import [java.nio.file Files]
                   [java.nio.file.attribute FileTime])))

(defn merge-meta

  "Non-throwing version of (vary-meta obj merge metamap-1 metamap-2 ...).
  Like `vary-meta`, this only applies to immutable objects. For
  instance, this function does nothing on atoms, because the metadata
  of an `atom` is part of the atom itself and can only be changed
  destructively."

  {:style/indent 1}
  [obj & metamaps]
  (try
    (apply vary-meta obj merge metamaps)
    #?(:clj (catch Exception _ obj)
       :cljs (catch js/Error _ obj))))

(defn disable-instrumentation [x]
  (merge-meta x
    {:cljs.storm/skip-fn-trace? true
     :cljs.storm/skip-expr-instrumentation? true}))

(defn clojure-form-source-hash

  "Hash a clojure form string into a 32 bit num.
  Meant to be called with printed representations of a form,
  or a form source read from a file."

  [s]
  (let [M 4294967291
        clean-s (-> s
                    (str/replace #"#[/.a-zA-Z0-9_-]+" "") ;; remove tags
                    (str/replace #"\^:[a-zA-Z0-9_-]+" "") ;; remove meta keys
                    (str/replace #"\^\{.+?\}" "")         ;; remove meta maps
                    (str/replace #";.+\n" "")             ;; remove comments
                    (str/replace #"[ \t\n]+" ""))         ;; remove non visible
        ] 
    (loop [sum 0
           mul 1
           i 0
           [c & srest] clean-s]
      (if (nil? c)
        (mod sum M)
        (let [mul' (if (= 0 (mod i 4)) 1 (* mul 256))
              sum' (+ sum (* (int c) mul'))]
          (recur sum' mul' (inc i) srest))))))

(defn obj-coord [kind obj]
  (str kind (clojure-form-source-hash (pr-str obj))))

(defn walk-code-form

  "Walk through form calling (f coor element).
  The value of coor is a vector of indices representing element's
  position in the form or a string for navigating into maps and set which
  are unordered. In the case of map elements, the string will start with a K or a V
  depending if it is a key or a value and will be followed by the hash of the key form for the entry.
  For sets it will always start with K followed by the hash of the element form.
  All metadata of objects in the form is preserved."
  
  ([f form] (walk-code-form [] f form))
  ([coord f form]
   (let [walk-sequential (fn [forms]
                           (->> forms
                                (map-indexed (fn [idx frm]
                                               (walk-code-form (conj coord idx) f frm)))))
         walk-set (fn [forms]
                    (->> forms
                         (map (fn [frm]                                
                                (walk-code-form (conj coord (obj-coord "K" frm)) f frm)))
                         (into #{})))
         walk-map (fn [m]
                    (reduce-kv (fn [r kform vform]
                                 (assoc r
                                        (walk-code-form (conj coord (obj-coord "K" kform)) f kform)
                                        (walk-code-form (conj coord (obj-coord "V" kform)) f vform)))
                               (empty m)
                               m))
         
         result (cond
                  
                  (and (map? form) (not (record? form))) (walk-map form)                  
                  (set? form)                            (walk-set form)
                  (list? form)                           (apply list (walk-sequential form))
                  (seq? form)                            (doall (walk-sequential form))
                  (coll? form)                           (into (empty form) (walk-sequential form))                                    
                  :else form)]
     
     (f coord (merge-meta result (meta form))))))

(defn tag-form-recursively
  "Recursively add coordinates to all forms"
  [form key]  
  (walk-code-form (fn [coor frm]
                    (if (or (symbol? frm)
                            (seq? frm))
                      (merge-meta frm {key coor})
                      frm))
                  form))

#?(:clj
   (defn touch-cljs-files [path]
     (doseq [f (file-seq (io/file path))]
       (when (.isFile f)
         (let [[_ fext] (re-find #".+\.([a-z]+)$" (.getName f))]
           (when (#{"cljc" "cljs"} fext)
             (println "Touching " (.getAbsolutePath f))
             (Files/setLastModifiedTime 
              (.toPath f) 
              (FileTime/fromMillis (System/currentTimeMillis)))))))))
