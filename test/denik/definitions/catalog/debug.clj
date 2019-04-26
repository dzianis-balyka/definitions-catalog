(ns denik.definitions.catalog.debug
  (:require
    [denik.definitions.catalog.core :as dbc]
    [denik.definitions.catalog.cassandraregistry :as dbcr]
    [clojure.tools.logging :as log]
    [denik.definitions.catalog.readers :as rr]
    [denik.definitions.catalog.exectx :as ectx]
    )
  (:import
    (com.datastax.driver.core.utils UUIDs)
    (java.util UUID))
  )
;(log/info (UUIDs/timeBased))
;(log/info (UUID/fromString "5d32ffc0-4642-11e9-ab95-4d847b81b4b4"))
;(def x (dbc/toAst "(prn (ns-interns *ns*)) (def a 1) (defn b [x] (* x x)) (prn (b 3)) (prn (b 5)) (b 55)"))

;(log/info x)

;(dbc/evalnv "asd" "v222" x)

;(log/info "!!!" (ns-interns (symbol (dbc/replsns "asd" "v222"))))
;(log/info "!!!" (get (ns-interns (symbol (dbc/replsns "asd" "v222"))) (symbol "statements")))
;(log/info "!!!" (var-get (get (ns-interns (symbol (dbc/replsns "asd" "v222"))) (symbol "statements"))))

(defn buildProcData [registry procId]
  ;load names + threads
  ;load cursor
  {
   :names   (dbc/?latest registry [""] [""] nil nil 1)
   :threads (dbc/?latest registry [""] [""] nil nil 1)
   :after   (dbc/?latest registry [""] [""] nil nil 1)
   }
  )

(defn processElement [e]
  ;save cursor
  (prn e)
  )

(log/info #reg/var [:qwe-eqwe-eqwe #reg/uuid "123213213"])


(with-open
  [registry (dbcr/buildCassandraRegistry ["localhost"] "registry")]
  ;(log/info (dbc/+version registry "test:/aaa" "test:/sessions/s1" "(dbc/?latest denik.definitions.catalog.exectx/registry [\"test:/aaa\"] [\"test:/sessions/s2\"] nil nil 1)"))
  ;(log/info (dbc/+version registry "test:/aaa" "test:/sessions/s2" "2"))
  ;(log/info (dbc/+version registry "test:/bbb" "test:/sessions/s4" "4"))
  ;(log/info (dbc/+version registry "test:/ggg" "test:/sessions/s5" "5"))
  ;(log/info (dbc/+version registry "test:/aaa" "test:/sessions/s1" "1"))
  ;(log/info (dbc/+version registry "test:/aaa" "test:/sessions/s2" "2"))
  ;(log/info (dbc/+version registry "test:/bbb" "test:/sessions/s4" "4"))
  ;(log/info (dbc/+version registry "test:/ggg" "test:/sessions/s5" "5"))
  ;;(log/info (dbc/?ns registry "test:/aaa" "test:/zzz"))
  ;(let
  ;  [
  ;   elements (dbc/?earliest registry ["test:/sessions/s1"] ["test:/aaa"] nil nil 5)
  ;   ]
  ;  (mapv #(log/info %) elements)
  ;  )
  ;(log/info (dbc/?latest registry nil ["test:/aaa"] nil nil 5))
  ;;(log/info (dbc/?version registry "test:/aaa" nil))
  ;
  ;(dbc/processVarValues registry ["test:/aaa"] ["test:/sessions/s1"] nil nil 5 processElement 1000)


  (let
    [
     v1 (dbc/+version registry "test:/regVarDep" "test:/threads/t1" "(* 2 2)")
     v2Value (str "(require '[denik.definitions.catalog.exectx :as ectx]) (apply ectx/fromReg '(\"test:/regVarDep\"  #uuid \"" v1 "\" nil))")
     v2 (dbc/+version
          registry
          "test:/regVar"
          "test:/threads/t1"
          v2Value
          )
     ]
    (log/info v1 v2)
    (binding
      [denik.definitions.catalog.exectx/registry registry]
      (log/info "!!!!!!" (ectx/fromReg "test:/regVar" v2 nil))
      )
    )

  ;(binding
  ;  [denik.bighead.exectx/registry registry]
  ;(log/info
  ;  (eval
  ;    '(some->
  ;       (dbc/?latest denik.bighead.exectx/registry ["test:/aaa"] ["test:/sessions/s1"] nil nil 1)
  ;       (first)
  ;       (:version)
  ;       (:value)
  ;       (read-string)
  ;       (eval)
  ;       )
  ;    )
  ;
  ;  )
  ;)


  ;(eval
  ;  '(dbc/?latest reg ["test:/aaa"] ["test:/sessions/s1"] nil nil 1)
  ;  )

  )
(log/info "")