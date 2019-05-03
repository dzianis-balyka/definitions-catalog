(ns denik.definitions.catalog.igniteregistry
  (:require
    [denik.definitions.catalog.ignite :as ignite]
    [clojure.tools.logging :as log]
    [clj-bean.core :as bc]
    )
  (:import (org.apache.ignite.configuration IgniteConfiguration DataStorageConfiguration DataRegionConfiguration CacheConfiguration)
           (org.apache.ignite Ignition)
           (denik.definitions.catalog.core DefinitionsRegistry)
           (com.datastax.driver.core.utils UUIDs)
           (org.apache.ignite.cache.query SqlFieldsQuery)
           (java.sql Timestamp)
           (java.util Date)))

(defn buildIgniteRegistry [schema]
  (try
    (let
      [
       dataRegionConfiguration (-> (new DataRegionConfiguration) (.setPersistenceEnabled true))
       dataStoreCfg (-> (new DataStorageConfiguration) (.setDefaultDataRegionConfiguration dataRegionConfiguration))
       igniteCfg (-> (new IgniteConfiguration) (.setDataStorageConfiguration dataStoreCfg) (.setActiveOnStart true))
       ignite (Ignition/start igniteCfg)
       cacheCfg (-> (new CacheConfiguration) (.setName schema))
       cache (.getOrCreateCache ignite cacheCfg)

       elementsTable :RegistryElements
       elementsIdx :RegistryElements
       ]
      (log/info "creating keyspace and table")

      (ignite/createElementsTableAndIndex
        cache
        )

      (reify
        DefinitionsRegistry
        (+version [this place chain edn]
          (let
            [timeuuid (UUIDs/timeBased)]
            (.query
              cache
              (->
                (new SqlFieldsQuery (str "INSERT INTO '" schema "'.Registry (id, ts, chain, place, edn) VALUES(?, ?,?,?,?)"))
                (.setArgs (into-array Object [timeuuid (new Timestamp (.timestamp timeuuid)) chain place edn]))
                )
              )
            timeuuid
            )
          )
        (?latest [this places chains afterVersion beforeVersion limit]
          "latest version of name"
          )
        (?earliest [this vars threads afterVersion beforeVersion limit]
          "earliest version of name at threads after specified version"
          )
        (?version [this versions]
          "concrete version"
          )
        (?names
          [this from to]
          )

        java.io.Closeable
        (close [this]
          (try
            (some-> cache (.close))
            (catch Throwable t
              (log/error t "unable to close ignite cache for registry")
              )
            )
          )

        )
      )
    (catch
      Throwable t
      (log/error t "unable to create ignite registry")
      nil
      )
    )
  )