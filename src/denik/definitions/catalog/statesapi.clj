(ns denik.definitions.catalog.statesapi
  (:require
    [clojure.tools.logging :as log]
    [denik.definitions.catalog.tagsapi :as tapi]
    [denik.definitions.catalog.core :as dbc]
    [denik.definitions.catalog.exectx :as exectx]
    )
  (:import (java.util UUID)))


(defn stateByVersion [placeVersion]
  (let
    [
     element (first (dbc/?v exectx/registry [placeVersion]))
     elementPlace (:place element)
     placeChain (exectx/loadChain (str elementPlace (UUID/randomUUID)) elementPlace (:chain element) nil nil (:ts element))
     lastState (last placeChain)
     ]
    lastState
    )
  )

(defn stateByTag
  (
   [place tag]
   (let
     [placeVersion (tapi/placeVersionByTag tag place)
      state (stateByVersion placeVersion)]
     state
     )
    )
  (
   [place tag var]
   (some->
     (stateByTag place tag)
     (get (symbol var))
     )
    )
  )
