(ns denik.definitions.catalog.interpreters.debugexperiments
  (:require
    [clojure.tools.logging :as log]
    [clojure.repl :as repl]
    )
  )

(let
  [
   coreInterns (ns-interns (find-ns 'clojure.core))
   sources (mapv #(first %) coreInterns)
   fe (first sources)
   ]
  (log/info sources)
  (log/info fe)
  (log/info (repl/source-fn fe))
  )