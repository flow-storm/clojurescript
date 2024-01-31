# ClojureScriptStorm

## Intro
Welcome to the ClojureScriptStorm repository. ClojureScriptStorm is a fork of the [official ClojureScript
compiler](https://github.com/clojure/clojurescript), with some extra code added to make it a dev compiler. 
This means a compiler with some extra capabilities tailored for development.

ClojureScriptStorm will add instrumentation (extra javascript) to trace everything that is happening as your programs
execute. You use it by providing a bunch of callbacks that ClojureScriptStorm will call as code runs.

## Starting a repl with ClojureStorm

```bash
clj -J-Dcljs.storm.instrumentOnlyPrefixes=dev -J-Dcljs.storm.instrumentEnable=true -Sdeps '{:paths ["src"] :deps {com.github.flow-storm/clojurescript {:mvn/version "RELEASE"}}}' -M -m cljs.main --repl
```

The important bits here are :

- add the ClojureScriptStorm dependency
- tell ClojureScriptStorm what namespaces to instrument via `instrumentOnlyPrefixes` in this case `dev`

## Hooking into ClojureStorm

```clojure
(set! cljs.storm.tracer/trace-fn-call-fn
      (fn [_ fn-ns fn-name fn-args-vec form-id]
        (prn "fn-call " fn-ns fn-name fn-args-vec form-id)))
(set! cljs.storm.tracer/trace-fn-return-fn
      (fn [_ ret coord form-id]
        (prn "fn-return" ret coord form-id)))
(set! cljs.storm.tracer/trace-fn-unwind-fn
      (fn [_ error coord form-id]
        (prn "fn-unwind" error coord form-id)))
(set! cljs.storm.tracer/trace-expr-fn
      (fn [_ val coord form-id]
        (prn "expr" val coord form-id)))
(set! cljs.storm.tracer/trace-bind-fn
      (fn [_ coord sym-name bind-val]
        (prn "bind" coord sym-name bind-val)))
(set! cljs.storm.tracer/trace-form-init-fn
      (fn [form-data]
        (prn "form-data" form-data)))
```

Once that is set, you could try something like this :

```clojure
user=> (ns dev)
...
dev=> (defn sum [a b] (+ a b))

"form-data" {:form-id -133716645, :ns "dev", :form (defn sum [a b] (+ a b)), :file nil, :line nil}

dev=> (sum 4 5)

"fn-call " "dev" "sum" #js {"0" 4, "1" 5} -133716645
"bind" "" "a" 4
"bind" "" "b" 5
"expr" 4 "3,1" -133716645
"expr" 5 "3,2" -133716645
"fn-return" 9 "3" -133716645
"expr" 9 "" -1067876745
"form-data" {:form-id -1067876745, :ns "dev", :form (sum 4 5), :file nil, :line nil}
"expr" "9" "" nil
9

```

## Forms and coordinates

The example above  shows your callbacks receiving form ids and coordinates, let's see how you can use them.

The form-id on each fn-call, fn-return and expr corresponds with the data received by on `trace-form-init-fn`.
This function will be called once, when the form is defined in the runtime.

Coords are strings with the coordinates inside the form tree.
In the case of our sum form, "2,1" means the third element (the `[a b]` vector), and then the first one `a`. 
Coordinates also work with unordered literals like sets, and maps with more than 8 keys.

If you want utility funcitons to work with forms and  coordinates take a look at
[get-form-at-coord](https://github.com/flow-storm/hansel/blob/master/src/hansel/utils.cljc#L74-L78) for example.

## Controlling instrumentation without restarting the repl

You can add/remove instrumentation prefixes without restarting the repl by calling :

```clojure
(cljs.storm.api/add-instr-prefix "my-app")
(cljs.storm.api/rm-instr-prefix "my-app")
```

## Applications using ClojureScriptStorm

* [FlowStorm debugger](http://www.flow-storm.org)
