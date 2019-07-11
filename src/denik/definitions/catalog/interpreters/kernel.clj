(ns denik.definitions.catalog.interpreters.kernel
  (:require
    [clojure.tools.logging :as log]
    [denik.definitions.catalog.core :as dbc]
    )
  )

(defprotocol Interpretation
  (stack [this] "current stack")
  (nextStep [this] "calculate next step")
  (stateHistory [this] "iterator of interpretations for each history point")
  (modifyState [this newState] "adds data to calculation branch")
  )

;find interpreter for x
;interpret
(defn interpret
  (
   [x intDict ctx]
   (cond
     (map? x) ((:map intDict) x intDict ctx)
     (vector? x) ((:vector intDict) x intDict ctx)
     (set? x) ((:set intDict) x intDict ctx)
     (list? x) ((:list intDict) x intDict ctx)
     (symbol? x) ((:symbol intDict) x intDict ctx)
     (number? x) ((:number intDict) x intDict ctx)
     (boolean? x) ((:boolean intDict) x intDict ctx)
     (string? x) ((:string intDict) x intDict ctx)
     :else nil)
    )
  ;(
  ; [x ctx]
  ; (interpret x defaultIntDict ctx)
  ;  )
  )


(defn defaultPrimitive [form intDict ctx]
  (log/info "primitive" form)
  form
  )

(defn defaultLogFn [form intDict ctx]
  (log/info form ctx))

(defn getFromGlobalNs)

(defn processNamedNsSymbol [form intDict ctx]
  )
(defn processContextSymbol [form intDict ctx]
  )

(defn defaultSymbol [form intDict ctx]
  (log/info form ctx)
  (if ( ))
  (let [sname ]
    (loop [c ctx]
      ()

      )
    )
  )

(defn defaultCollection [form intDict ctx]
  (log/info "processing collection" form)
  (map #(interpret % intDict ctx) form)
  )

(defn defaultVector [form intDict ctx]
  (log/info "processing vector" form)
  (vec (defaultCollection form intDict ctx)))

(defn defaultSet [form intDict ctx]
  (log/info "processing set" form)
  (set (defaultCollection form intDict ctx)))

(defn defaultMap [form intDict ctx]
  (log/info "processing map" form)
  (into {} (map #(vector (interpret (first %) intDict ctx) (interpret (last %) intDict ctx)) form))
  )

(defn listProcessor [form intDict ctx]
  (log/info "processing list" form ctx)
  )

(def defaultIntDict
  {:map     defaultMap
   :vector  defaultVector
   :set     defaultSet
   :list    listProcessor
   :symbol  defaultSymbol
   :number  defaultPrimitive
   :boolean defaultPrimitive
   :string  defaultPrimitive})





(defn processRepl [registry processorChain thread]
  ;stack place
  (let [
        stack (dbc/?< registry ["exe/stack"] [processorChain] [thread] nil nil 1)
        ]
    )
  )
