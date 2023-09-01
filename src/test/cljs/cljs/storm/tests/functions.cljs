(ns cljs.storm.tests.functions)

(defn empty-body [a b])

(defn simple [a b]
  (+ a b))

(def defed (fn [] 42))

(defn multi-arity
  ([a] (multi-arity a 2))
  ([a b] (+ a b)))

(defn args-destructuring [{:keys [a b]}]
  (+ a b))

(defn variadic [& nums]
  (reduce + nums))

(defn tail-recursive [s n]
  (if (zero? n)
    s
    (recur (+ s n) (dec n))))

(defn factorial [n]
  (if (zero? n)
    1
    (* n (factorial (dec n)))))

(defmulti area :type)

(defmethod area :square
  [{:keys [side]}]
  (* side side))
