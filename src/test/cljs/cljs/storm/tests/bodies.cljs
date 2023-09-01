(ns cljs.storm.tests.bodies)

(defn tried []
  (try
    (+ 1 1)
    (throw (js/Error. "Dummy"))
    (catch js/Error e
      (+ 2 2))))

(defn letfn-fn []
  (letfn [(square [x]
            (* x x))]
    (+ 1 (square 2))))

(defn looper []
  (loop [s 0
         n 2]
    (if (zero? n)
      s
      (recur (+ s n) (dec n)))))

(defn letter []
  (let [a 5
        b (* a 2)
        c (let [z (+ a b)]
            z)]
    c))

(defn casey [x]
  (case x
    :first (+ 40 2)
    :second 1
    0))

(defn doer []
  (do
    (+ 1 1)
    (+ 2 2)
    (do
      (+ 3 3)
      (+ 4 4))))

(defn setter []
  (set! js/a 2)
  (set! js/b 40)
  (+ js/a js/b))

(defn interopter [o]
  (+ (.-num o) (.f o 40)))
