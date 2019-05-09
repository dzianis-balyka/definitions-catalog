(ns denik.definitions.catalog.ignite
  (:require
    [clojure.tools.logging :as log]
    )
  (:import (org.apache.ignite Ignite Ignition)
           (org.apache.ignite.configuration IgniteConfiguration CacheConfiguration DataRegionConfiguration DataStorageConfiguration)
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

(defn createElementsTableAndIndex [cache schema table]


  (log/info (.getAll (.query
                       cache
                       (new SqlFieldsQuery (format "CREATE TABLE IF NOT EXISTS %1$s.%2$s (id uuid, ts timestamp, chain varchar, place varchar, thread varchar, edn varchar,  PRIMARY KEY (id))" (name schema) (name table)))
                       )))
  (log/info (.getAll (.query
                       cache
                       (new SqlFieldsQuery (format "CREATE INDEX IF NOT EXISTS idx_%2$s_ts_id_chain_place ON %1$s.%2$s (ts,id,chain,place,thread)" (name schema) (name table)))
                       )))

  )
