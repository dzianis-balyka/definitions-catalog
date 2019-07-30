(ns denik.definitions.catalog.interpreters.ctx
  (:require
    [clojure.tools.logging :as log]
    [denik.definitions.catalog.exectx :as exectx]
    [denik.definitions.catalog.core :as dbc])
  )


(def CTX_PREFIX "ctx:")

(defn ctxPlace [ctxName] (str CTX_PREFIX ctxName))

()