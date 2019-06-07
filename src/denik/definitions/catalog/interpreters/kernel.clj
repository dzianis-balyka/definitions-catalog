(ns denik.definitions.catalog.interpreters.kernel
  (:require
    [clojure.tools.logging :as log]
    [denik.definitions.catalog.core :as dbc]
    )
  )

(defprotocol Interpretation
  (stack [this] "current stack")
  (nextStep [this ] "calculate next step")
  (stateHistory [this] "iterator of interpretations for each history point")
  (modifyState [this newState] "adds data to calculation branch")
  )

;find interpreter for x
;interpret
(defn
  (interpret
    [x intDict]
    (cond
      (map? x) (log/info "map" x)
      (vector? x) (log/info "vector" x)
      (set? x) (log/info "set" x)
      (list? x) (log/info "list" x)
      :else (log/info "unknown" x))
    )
  (interpret
    [x]
    (interpret x nil)
    )
  )



(defn processRepl [registry processorChain thread]
  ;stack place
  (let [
        stack (dbc/?< registry ["exe/stack"] [processorChain] [thread] nil nil 1)
        ]
    )
  )
