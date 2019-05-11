(ns denik.definitions.catalog.interpreters.kernel
  (:require
    [clojure.tools.logging :as log]
    [denik.definitions.catalog.core :as dbc]
    )
  )


(defn processExpression [x]

  )

(defn processRepl [registry processorChain thread]
  ;stack place
  (let [
        stack (dbc/?< registry ["exe/stack"] [processorChain] [thread] nil nil 1)
        ]
    )
  )
