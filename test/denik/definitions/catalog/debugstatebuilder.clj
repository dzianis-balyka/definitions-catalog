(ns denik.definitions.catalog.debugstatebuilder
  (:require [denik.definitions.catalog.statebuilder :refer :all]
            [clojure.tools.logging :as log]
            [denik.definitions.catalog.igniteregistry :as reg]
            [denik.definitions.catalog.exectx :as exectx]
            [denik.definitions.catalog.core :as dbc])
  (:import (java.util Iterator)))



(with-open
  [registry (reg/buildIgniteRegistry "REGISTRY_EXTENDED")]


  ;(dbc/+v registry "schema:a.b.c" "schema:a.b.c/master" "thread:root" "(def schemas [])")
  ;(dbc/+v registry "schema:a.b.c" "schema:a.b.c/master" "thread:root" "(def schemas (conj schemas \"s1\"))")
  ;(dbc/+v registry "schema:a.b.c" "schema:a.b.c/master" "thread:root" "(def schemas (conj schemas \"s2\"))")

  (binding
    [
     denik.definitions.catalog.exectx/registry registry
     ]

    ;(log/info (exectx/chainToSeq ["test:/place"] ["test:/chain"] ["test:/thread"] nil nil 2))

    (mapv #(log/info "!!!" %) (take 3 (exectx/chainToSeq ["schema:a.b.c"] ["schema:a.b.c/master"] ["thread:root"] nil nil 2)))

    )
  )
