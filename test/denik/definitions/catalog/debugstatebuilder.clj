(ns denik.definitions.catalog.debugstatebuilder
  (:require [denik.definitions.catalog.statebuilder :refer :all]
            [clojure.tools.logging :as log]
            [denik.definitions.catalog.igniteregistry :as reg]
            [denik.definitions.catalog.exectx :as exectx]
            )
  (:import (java.util Iterator)))



(with-open
  [registry (reg/buildIgniteRegistry "REGISTRY_EXTENDED")]

  (log/info registry)

  (binding
    [
     denik.definitions.catalog.exectx/registry registry
     ]
    (log/info exectx/registry)
    (mapv #(prn "!!!" %) (take 200 (exectx/chainToSeq ["test:/place"]  ["test:/chain"] ["test:/thread"] nil nil 100 )))

    )
  )
