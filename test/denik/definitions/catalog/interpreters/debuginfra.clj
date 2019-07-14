(ns denik.definitions.catalog.interpreters.debuginfra
  (:require
    [clojure.tools.logging :as log]
    [denik.definitions.catalog.interpreters.infrastructure :as infra]
    [denik.definitions.catalog.igniteregistry :as reg]
    )
  )

(with-open
  [registry (reg/buildIgniteRegistry "REGISTRY_EXTENDED")]
  (binding
    [
     denik.definitions.catalog.exectx/registry registry
     ]
    (infra/addThreadToRegistry "first" {:in {:place "first:in" :chain "chain:io"}
                                        :out {:place "first:out" :chain "chain:io"}
                                        :state {:place "first:state" :chain "first:track"}})
    (log/info (infra/threadsRegistry))
    (infra/runThread "first")
    )

  )

;(let
;  [
;   e `(let
;        [x# 1 y# 2]
;        (prn (+ x# y#))
;        (let
;          [x# 5 z# 8]
;          (+ x# y# z#)
;          )
;        )
;   ]
;  (log/info e)
;  (log/info (eval e))
;  )

