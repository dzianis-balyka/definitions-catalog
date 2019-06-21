(ns denik.definitions.catalog.debugstatesapi
  (:require
    [clojure.tools.logging :as log]
    [denik.definitions.catalog.statesapi :refer :all]
    [denik.definitions.catalog.exectx :as exectx]
    [denik.definitions.catalog.igniteregistry :as reg]
    [denik.definitions.catalog.core :as dbc]
    [denik.definitions.catalog.tagsapi :as tapi])
  (:import (java.util UUID)))


(with-open
  [registry (reg/buildIgniteRegistry "REGISTRY_EXTENDED")]
  (binding
    [
     denik.definitions.catalog.exectx/registry registry
     ]

    (let
      [
       tag "t:v1"

       chain "chain:1"
       place "place:f1"
       place2 "place:f1"
       thread "root"

       ;elementId (dbc/+v registry place chain thread
       ;                  (str (quote (def fx (quote (fn [x] (* x x)))))))

       ]
      ;(tapi/initTagMut tag)
      ;(tapi/tag tag {place elementId})

      ;(stateByTag place tag)

      (dbc/+v registry place chain thread
              (str
                `(intern *ns* (symbol "res") ((eval (stateByTag ~place ~tag "fx")) 2))
                )
              )


      ;(log/info (stateByTag place tag "fx"))
      (run! #(log/info %) (exectx/chainToSeq [place2] [chain] [thread] nil nil 100))
      (log/info (last (exectx/loadChain (str place2 (UUID/randomUUID)) place2 chain thread)))

      )

    )

  )
