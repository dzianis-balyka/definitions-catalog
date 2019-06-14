(ns denik.definitions.catalog.debugstatebuilder
  (:require [denik.definitions.catalog.statebuilder :refer :all]
            [clojure.tools.logging :as log]
            [clojure.string :as str]
            [denik.definitions.catalog.igniteregistry :as reg]
            [denik.definitions.catalog.exectx :as exectx]
            [denik.definitions.catalog.schemaapi :refer :all]
            [denik.definitions.catalog.core :as dbc])
  (:import (java.util Iterator)))



;(with-open
;  [registry (reg/buildIgniteRegistry "REGISTRY_EXTENDED")]
;
;
;  ;(dbc/+v registry "schema:a.b.c" "schema:a.b.c/master" "thread:root" "(def schemas [])")
;  ;(dbc/+v registry "schema:a.b.c" "schema:a.b.c/master" "thread:root" "(def schemas (conj schemas \"s1\"))")
;  ;(dbc/+v registry "schema:a.b.c" "schema:a.b.c/master" "thread:root" "(def schemas (conj schemas \"s2\"))")
;  (dbc/+v registry "schema:com.ds1" "schema:com.ds1/master" "thread:root" "(def schemas [])")
;  (dbc/+v registry "schema:com.ds1" "schema:com.ds1/master" "thread:root" "(require '(denik.definitions.catalog.schemaapi :as sapi)) (def schemas (conj schemas (->(sapi/+s \"Event\" [])(sapi/+f \"f1\" \"string\")(sapi/+f \"f2\" \"int\"))))")
;  ;(dbc/+v registry "function:x" "function:x/master" "thread:root" "(def f-definition (quote (fn [x] (* x x))))")
;  ;(dbc/+v registry "function:x" "function:x/master" "thread:root" "(def f-definition (quote (fn [x] (* x x))))")
;
;  (binding
;    [
;     denik.definitions.catalog.exectx/registry registry
;     ]
;
;    ;(log/info (exectx/chainToSeq ["test:/place"] ["test:/chain"] ["test:/thread"] nil nil 2))
;
;    (let
;      [chain (exectx/chainToSeq ["schema:com.ds1"] ["schema:com.ds1/master"] ["thread:root"] nil nil 2)
;       asts (map #(dbc/toAst (:edn %)) chain)
;       namespaceName "ttt"
;       ]
;      (mapv #(log/info "!!!" %) chain)
;      (mapv #(log/info %) asts)
;      (mapv #(log/info (exectx/evalInNs namespaceName %) (deref (get (ns-interns (symbol namespaceName)) (symbol "schemas") )) ) asts)
;      )
;    )
;  )

(defmacro ednStr [& form]
  (str/join (mapv #(str %) form))
  )



(let
  [chain
   [

    {:id  nil :ts nil :chain nil :place nil :thread nil
     :edn (ednStr
            (require (quote [denik.definitions.catalog.schemaapi :refer :all]))
            )}
    {:id  nil :ts nil :chain nil :place nil :thread nil
     :edn (ednStr
            (addSchema
              (->
                (createSchema "Event" [])
                (addField "f1" "string")
                (addField "f2" "int")
                )
              )
            )
     }
    {:id  nil :ts nil :chain nil :place nil :thread nil
     :edn (ednStr
            (addSchema
              (->
                (createSchema "Person" [])
                (addField "f1" "string")
                (addField "f2" "int")
                )
              )
            )
     }
    ]
   asts (map #(dbc/toAst (:edn %)) chain)
   namespaceName "ttt"
   ]
  (mapv #(log/info "!!!" %) chain)
  (mapv #(log/info %) asts)
  (mapv
    #(log/info
       (exectx/evalInNs namespaceName %)
       (some->
         (get (ns-interns (symbol namespaceName)) (symbol "schemas"))
         (deref)
         )
       )
    asts
    )
  )
