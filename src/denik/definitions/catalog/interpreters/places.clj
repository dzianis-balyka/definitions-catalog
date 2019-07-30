(ns denik.definitions.catalog.interpreters.places
  (:require
    [clojure.tools.logging :as log]
    [denik.definitions.catalog.exectx :as exectx]
    [denik.definitions.catalog.ast :as ast]
    [denik.definitions.catalog.core :as dbc])
  (:import (java.util UUID)))

(defn tempPlaceNs [place] (str (UUID/randomUUID) "::" place))

(defn placeMutationsJournal
  ([place chain thread] (map ast/toAst (exectx/chainToSeq [place] [chain] [thread] nil nil 50)))
  ([place chain] (map ast/toAst (exectx/chainToSeq [place] [chain] nil nil nil 50)))
  )

(defn placeStatesJournal
  ([place chain]
   (let [ns tempPlaceNs]
     (exectx/evalInNs ns (placeMutationsJournal place chain))
     )
    )
  ([place chain thread]
   (let [ns tempPlaceNs]
     (exectx/evalInNs ns (placeMutationsJournal place chain threads))
     )
    )

  )

