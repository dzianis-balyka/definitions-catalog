(ns denik.definitions.catalog.igniteregistry
  (:require
    [denik.definitions.catalog.ignite :as ignite]
    [clojure.string :as str]
    [clojure.tools.logging :as log]
    [clj-bean.core :as bc]
    )
  (:import (org.apache.ignite.configuration IgniteConfiguration DataStorageConfiguration DataRegionConfiguration CacheConfiguration)
           (org.apache.ignite Ignition)
           (denik.definitions.catalog.core DefinitionsRegistryExtended)
           (com.datastax.driver.core.utils UUIDs)
           (org.apache.ignite.cache.query SqlFieldsQuery)
           (java.sql Timestamp)
           (java.util Date)))

(defn rangeSearch [schema elementsTable cache places chains threads afterVersion beforeVersion limit asc?]
  (let
    [
     placesPart (some-> places (seq) ((fn [es] (format "PLACE IN (%1$s)" (str/join ", " (mapv (fn [e] "?") es))))))
     chainsPart (some-> chains (seq) ((fn [es] (format "CHAIN IN (%1$s)" (str/join ", " (mapv (fn [e] "?") es))))))
     threadsPart (some-> threads (seq) ((fn [es] (format "THREAD IN (%1$s)" (str/join ", " (mapv (fn [e] "?") es))))))
     afterPart (if (nil? afterVersion) nil (format "TS > ?"))
     beforePart (if (nil? beforeVersion) nil (format "TS < ?"))
     whereCandidats (filter some? [placesPart chainsPart threadsPart afterPart beforePart])
     wherePart (if (empty? whereCandidats) "" (str "WHERE " (str/join " AND " whereCandidats)))
     limitPart "LIMIT ?"
     sortPart (if asc? "ASC" "DESC")
     ]
    (.getAll
      (.query
        cache
        (->
          (new
            SqlFieldsQuery
            (format "SELECT * FROM %1$s.%2$s %3$s ORDER BY TS %4$s %5$s" schema (name elementsTable) wherePart sortPart limitPart)
            )
          (.setArgs (into-array Object (concat (sequence places) (sequence chains) (sequence threads) (if (nil? afterVersion) [] [afterVersion]) (if (nil? beforeVersion) [] [beforeVersion]) [limit])))
          )
        )
      )
    )
  )

(defn buildIgniteRegistry [schema]
  (try
    (let
      [
       dataRegionConfiguration (-> (new DataRegionConfiguration) (.setPersistenceEnabled true))
       dataStoreCfg (-> (new DataStorageConfiguration) (.setDefaultDataRegionConfiguration dataRegionConfiguration))
       igniteCfg (-> (new IgniteConfiguration) (.setDataStorageConfiguration dataStoreCfg) (.setActiveOnStart true))
       ignite (doto (Ignition/start igniteCfg) (.active true))
       cacheCfg (-> (new CacheConfiguration) (.setName schema))
       cache (.getOrCreateCache ignite cacheCfg)

       elementsTable :RegistryElements
       ]
      (log/info "creating keyspace and table")

      (ignite/createElementsTableAndIndex
        cache
        schema
        elementsTable
        )



      (reify
        DefinitionsRegistryExtended
        (+v [this place chain thread definition] "add new version of definition for place in chain from thread "
          (let
            [uuid (UUIDs/timeBased)]
            (.query
              cache
              (->
                (new SqlFieldsQuery (format "INSERT INTO %1$s.%2$s (id, ts, chain,place, thread,edn) VALUES(?,CURRENT_TIMESTAMP(),?,?,?,?)" schema (name elementsTable)))
                (.setArgs (into-array Object [uuid chain place thread definition]))
                )
              )
            uuid
            )
          )
        (?< [this places chains threads afterVersion beforeVersion limit] "latest versions of places"
          (rangeSearch schema elementsTable cache places chains threads afterVersion beforeVersion limit false)
          )
        (?> [this places chains threads afterVersion beforeVersion limit] "earliest versions of places"
          (rangeSearch schema elementsTable cache places chains threads afterVersion beforeVersion limit true)
          )
        (?v [this versions] "concrete versions"
          (.getAll
            (.query
              cache
              (->
                (new SqlFieldsQuery (format "SELECT * FROM %1$s.%2$s WHERE ID IN (%3$s)" schema (name elementsTable) (str/join ", " (mapv (fn [e] "?") versions))))
                (.setArgs (into-array Object versions))
                )
              ))
          )

        java.io.Closeable
        (close [this]
          (log/info "closing ignite cache")
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