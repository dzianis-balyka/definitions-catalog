(ns denik.definitions.catalog.tagsapi
  (:require [clojure.tools.logging :as log]
            [denik.definitions.catalog.exectx :as exectx])
  (:import (java.util UUID)))

(def tagValuesVarName "values")

(defn tagPlace [tag] (str "tags:" tag))
(defn tagChain [tag] (str "chain:tag:" tag))
(defn tagThread [tag] (str "thread:tag:" tag ":root"))

(defn loadTag [tag]
  (let
    [
     tempSpace (str tag (UUID/randomUUID))
     state (last (exectx/loadChain tempSpace (tagPlace tag) (tagChain tag) (tagThread tag)))
     ]
    (remove-ns (symbol tempSpace))
    state
    )
  )

(defn placeVersionByTag [tag place]
  (some->
    (loadTag tag)
    (get (symbol tagValuesVarName))
    (get place)
    )
  )

(defn initTagMut [tag]
  (exectx/addToPlaceInChain
    (tagChain tag) (tagPlace tag) (tagThread tag)
    [(exectx/ednStr
       (when (not (contains? (ns-interns *ns*) (symbol denik.definitions.catalog.tagsapi/tagValuesVarName)))
         (intern *ns* (symbol denik.definitions.catalog.tagsapi/tagValuesVarName) {})
         )
       )]
    )
  )

(defn tagMut [placeToVersions]
  `(intern *ns* (symbol ~tagValuesVarName) (into @(get (ns-interns *ns*) (symbol ~tagValuesVarName)) ~placeToVersions))
  )

(defn tag [tag placeToVersion]
  (exectx/addToPlaceInChain
    (tagChain tag) (tagPlace tag) (tagThread tag)
    [(str (tagMut placeToVersion))]
    )
  )