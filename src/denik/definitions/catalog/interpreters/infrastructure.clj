(ns denik.definitions.catalog.interpreters.infrastructure
  (:require
    [clojure.tools.logging :as log]
    [denik.definitions.catalog.exectx :as exectx]
    [denik.definitions.catalog.core :as dbc])
  )


(def ThreadRegistryLocation {:place "threads-registry" :chain "root"})
(def SystemThreadName "thread:system")
(def ThreadRegistryVarName "threads")

(defn threadsRegistry []
  (let
    [
     lastSpaceState (last (exectx/loadChain "tempSpace" (:place ThreadRegistryLocation) (:chain ThreadRegistryLocation) nil))
     ]
    (get lastSpaceState (symbol ThreadRegistryVarName))
    )
  )

(defn getVarValue [varName defaultValue]
  (let
    [currentRegistry (some-> (ns-interns *ns*) (get (symbol varName)) (deref))]
    (if (nil? currentRegistry) defaultValue currentRegistry)
    )
  )

(defn addThreadToRegistry [threadName threadParams]
  (let
    [edn (str
           `(intern *ns* (symbol ~ThreadRegistryVarName) (assoc (getVarValue ~ThreadRegistryVarName {}) ~threadName ~threadParams))
           )
     ]
    (log/info "EDN!" edn)
    (dbc/+v
      exectx/registry
      (:place ThreadRegistryLocation)
      (:chain ThreadRegistryLocation)
      SystemThreadName
      edn
      )
    )
  )

(defn removeThreadFromRegistry [threadName]
  (let
    [edn (str
           `(intern *ns* (symbol ~ThreadRegistryVarName) (dissoc (getVarValue ~ThreadRegistryVarName {}) ~threadName))
           )
     ]
    (log/info "EDN!" edn)
    (dbc/+v
      exectx/registry
      (:place ThreadRegistryLocation)
      (:chain ThreadRegistryLocation)
      SystemThreadName
      edn
      )
    )
  )

(defn nsForPlaceInThread [prefix place thread]
  (str prefix "-" place "-" thread))

(defn runThread [threadName]

  (let
    [
     threadData (some-> (threadsRegistry) (get threadName))
     inPlace (:in threadData)
     outPlace (:out threadData)
     statePlace (:state threadData)
     interpreterPlace (:interpreter threadData)
     processState (last (exectx/loadChain (nsForPlaceInThread "treadState" (:place statePlace) threadName) (:place statePlace) (:chain statePlace) nil))
     currentStatement (some-> processState (:current))
     ]



    (log/info "thread data" threadData)
    (log/info "state" processState)
    )

  )


