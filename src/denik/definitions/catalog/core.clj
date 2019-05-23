(ns denik.definitions.catalog.core
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io])
  (:import (java.nio.charset StandardCharsets)))

(defprotocol DefinitionsRegistry
  (+version [this name thread definition] "add new version of definition for name")
  (?latest [this vars threads afterVersion beforeVersion limit] "latest version of name at session")
  (?earliest [this vars threads afterVersion beforeVersion limit] "earliest version of name at thread")
  (?version [this versions] "concrete versions")
  (?names [this from to] "list names by range [from to)")
  )

(defprotocol DefinitionsRegistryExtended
  (+v [this place chain thread definition] "add new version of definition for place in chain from thread ")
  (?< [this places chains threads afterVersion beforeVersion limit] "latest versions of places")
  (?> [this places chains threads afterVersion beforeVersion limit] "earliest versions of places")
  (?v [this versions] "concrete versions")
  )

(defprotocol SearchService
  (ednToDoc [edn]))

(defrecord Version [name version session value])
(defrecord IndexDoc [name version definition])


(defn toAst [s]
  (let
    [
     pbr (java.io.PushbackReader. (io/reader (.getBytes s StandardCharsets/UTF_8)))
     stmtsSeq (take-while #(nil? (second %)) (repeatedly #(try [(read pbr) nil] (catch Throwable t [nil t]))))
     stmts (map #(first %) stmtsSeq)
     ]
    (vec stmts)
    )
  )

(defn varsns [name version] (str "vars." name "." version))
(defn replsns [name version] (str "repls." name "." version))

(defn evalnv [name version asts]
  (let
    [
     previousNs *ns*
     varsNsSymbol (symbol (varsns name version))
     replsNsSymbol (symbol (replsns name version))
     replResults (try
                   (in-ns varsNsSymbol)
                   (refer 'clojure.core)
                   (mapv #(eval %) asts)
                   (catch Throwable t
                     (log/error t "error during ns evaluation for " name " and version " version)
                     nil
                     )
                   )
     ]
    (in-ns (symbol replsNsSymbol))
    (intern (symbol replsNsSymbol) (symbol "statements") asts)
    (intern (symbol replsNsSymbol) (symbol "results") replResults)
    (in-ns (ns-name previousNs))
    replResults
    )
  )

(defn ?vs
  ;([registry name filter] "latest version by filter")
  ([registry version var] "var from specified version namespace"
    ;(?version )
    )
  )

(defn processVarValues [registry vars threads fromVersion toVersion batchSize f waitMillis]
  (loop [after fromVersion]
    (let
      [elements (?earliest registry vars threads after toVersion batchSize)
       latestVersion (some-> (last elements) (:element) (:version))
       latest (if (some? latestVersion) latestVersion after)
       ]
      (mapv f elements)
      (when (empty? elements)
        (log/info "waiting")
        )
      (recur latest)
      )
    )
  )