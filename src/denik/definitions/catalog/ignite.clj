(ns denik.definitions.catalog.ignite
  (:require
    [clojure.tools.logging :as log]
    )
  (:import (org.apache.ignite Ignite Ignition)
           (org.apache.ignite.configuration IgniteConfiguration CacheConfiguration DataRegionConfiguration DataStorageConfiguration)
           (denik.definitions.catalog.igniteregistry RegistryElement)
           (java.util UUID LinkedHashMap Date)
           (org.apache.ignite.cache QueryEntity QueryIndex QueryIndexType)
           (org.apache.ignite.cache.query SqlFieldsQuery SqlQuery)
           (com.datastax.driver.core.utils UUIDs)
           (java.sql Timestamp))

  )

(defn doWithLocalCluster [f]
  (let
    [dataRegionConfiguration (-> (new DataRegionConfiguration) (.setPersistenceEnabled true))
     dataStoreCfg (-> (new DataStorageConfiguration) (.setDefaultDataRegionConfiguration dataRegionConfiguration))
     igniteCfg (-> (new IgniteConfiguration) (.setDataStorageConfiguration dataStoreCfg) (.setActiveOnStart true))
     ]
    (with-open [ignite (Ignition/start igniteCfg)]
      (f ignite)
      )
    )
  )

(defn createElementsTableAndIndex [cache]


  (log/info (.getAll (.query
                       cache
                       (new SqlFieldsQuery "CREATE TABLE IF NOT EXISTS REGISTRY.Registry2 (id uuid, ts timestamp, chain varchar, place varchar, edn varchar,  PRIMARY KEY (id))")
                       )))
  (log/info (.getAll (.query
                       cache
                       (new SqlFieldsQuery "CREATE INDEX IF NOT EXISTS idx_ts_id_chain_place ON REGISTRY.Registry2 (ts,id,chain,place)")
                       )))

  )
