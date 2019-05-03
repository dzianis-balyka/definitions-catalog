(ns denik.definitions.catalog.debugignite
  (:require
    [denik.definitions.catalog.ignite :as ign]
    [clojure.tools.logging :as log]
    )
  (:import (org.apache.ignite Ignite Ignition)
           (org.apache.ignite.configuration IgniteConfiguration CacheConfiguration)
           (denik.definitions.catalog.igniteregistry RegistryElement)
           (java.util UUID LinkedHashMap Date)
           (org.apache.ignite.cache QueryEntity QueryIndex QueryIndexType)
           (org.apache.ignite.cache.query SqlFieldsQuery SqlQuery)
           (com.datastax.driver.core.utils UUIDs)
           (java.sql Timestamp)))


(ign/doWithLocalCluster
  (fn [ign]
    (let
      [
       fields (doto
                (new LinkedHashMap)
                (.put "ID" (.getName java.util.UUID))
                (.put "TS" (.getName java.sql.Timestamp))
                (.put "CHAIN" (.getName String))
                (.put "PLACE" (.getName String))
                (.put "EDN" (.getName String))
                )
       idx (-> (new QueryIndex ["ID" "CHAIN" "PLACE"] (QueryIndexType/SORTED)) (.setName "IdxIdChainPlace"))
       queryEntity (-> (new QueryEntity (.getName java.util.UUID) "RegistryElement") (.setFields fields) (.setIndexes [idx]))
       ;cacheCfg (-> (new CacheConfiguration)  (.setIndexedTypes (into-array [UUID RegistryElement])))
       cacheCfg (-> (new CacheConfiguration) (.setName "Registry") (.setQueryEntities [queryEntity]))
       cache (.getOrCreateCache ign cacheCfg)
       ts (System/currentTimeMillis)
       uuidEnd (UUIDs/endOf ts)
       uuidStart (UUIDs/startOf ts)
       ]
      (log/info "cache" cache)

      ;(log/info (.getAll (.query
      ;                     cache
      ;                     (new SqlFieldsQuery "CREATE TABLE IF NOT EXISTS Registry (id uuid, , chain varchar, place varchar, edn varchar,  PRIMARY KEY (id))")
      ;                     )))
      ;(log/info (.getAll (.query
      ;                     cache
      ;                     (new SqlFieldsQuery "CREATE TABLE IF NOT EXISTS REGISTRY.Registry2 (id uuid, ts timestamp, chain varchar, place varchar, edn varchar,  PRIMARY KEY (id))")
      ;                     )))
      ;(log/info (.getAll (.query
      ;                     cache
      ;                     (new SqlFieldsQuery "CREATE INDEX IF NOT EXISTS idx_ts_id_chain_place ON REGISTRY.Registry2 (ts,id,chain,place)")
      ;                     )))
      (log/info (.getAll (.query
                           cache
                           (new SqlFieldsQuery "SELECT * FROM INFORMATION_SCHEMA.TABLES")
                           )))


      (.query
        cache
        (->
          (new SqlFieldsQuery "INSERT INTO REGISTRY.Registry2 (id, ts, chain,place,edn) VALUES(?, ?,?,?,?)")
          (.setArgs (into-array Object [(UUIDs/timeBased) (new Timestamp (System/currentTimeMillis)) "c1" "p1" (str (new Date))]))
          )
        )

      (mapv
        (fn [e]

          (log/info e)
          )

        (.getAll
          (.query
            cache
            (->
              (new SqlFieldsQuery "select * from REGISTRY.Registry2 where ts > ? order by ts")
              (.setArgs (into-array Object [(new Timestamp ts)]))
              )
            )
          ))
      )
    )
  )