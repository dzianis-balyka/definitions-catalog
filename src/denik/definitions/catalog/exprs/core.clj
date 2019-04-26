(ns denik.definitions.catalog.exprs.core
  (:require [clojure.tools.logging :as log])
  )

(defprotocol ExpressionsRegistry
  (+expr [this thread expr] "add new expr to thread")
  (?latest [this threads afterVersion beforeVersion limit] "latest expressions from thread")
  (?earliest [this threads afterVersion beforeVersion limit] "earliest expressions from thread")
  (?version [this versions] "concrete expression")
  )
