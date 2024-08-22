(ns cljs.storm.tracer)

(def trace-expr-fn nil)
(def trace-fn-call-fn nil)
(def trace-fn-return-fn nil)
(def trace-fn-unwind-fn nil)
(def trace-bind-fn nil)
(def trace-form-init-fn nil)

(defn dbg [& args]
  (js/console.log (apply pr-str args)))

(defn trace-fn-call [fn-args fn-ns fn-name form-id]
  #_(dbg "trace-fn-call" [fn-args fn-ns fn-name form-id])
  (when trace-fn-call-fn
    (trace-fn-call-fn nil fn-ns fn-name fn-args form-id)))

(defn trace-expr [val coord form-id]
  #_(dbg "trace-expr" [val coord form-id])
  (when trace-expr-fn
    (trace-expr-fn nil val coord form-id))
  val)

(defn trace-fn-return [ret-val coord form-id]
  #_(dbg "trace-fn-return" [ret-val coord form-id])
  (when trace-fn-return-fn
    (trace-fn-return-fn nil ret-val coord form-id))
  ret-val)

(defn trace-fn-unwind [error coord form-id]
  #_(dbg "trace-fn-unwind" [error coord form-id])
  (when trace-fn-unwind-fn
    (trace-fn-unwind-fn nil error coord form-id)))

(defn trace-bind [val coord sym-name]
  #_(dbg "trace-bind" [val coord sym-name])
  (when trace-bind-fn
    (trace-bind-fn nil coord sym-name val)))

(defn register-form [form-id form-ns emitted-coords form]
  #_(dbg "Registering form" form-id form-ns form)
  (when trace-form-init-fn
    (let [form-data {:form-id form-id
                     :ns form-ns
                     :form form
                     :emitted-coords emitted-coords
                     :file nil
                     :line nil}]
      (trace-form-init-fn form-data)))
  nil)
