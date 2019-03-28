(ns denik.definitions.catalog.cassandraregistry
  (:require [denik.definitions.catalog.cassandra :as cassandra]
            [denik.definitions.catalog.core :as bhcore]
            [qbits.alia :as alia]
            [qbits.hayt :as hayt]
            [qbits.hayt.dsl.statement :as stmt]
            [qbits.hayt.dsl.clause :as cls]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            )
  (:import (denik.definitions.catalog.core DefinitionsRegistry)
           (com.datastax.driver.core.utils UUIDs))
  )

(defn joinWithVersion [registry elements]
  (let
    [
     versions (mapv :version elements)
     joined (apply hash-map (flatten (map (fn [e] [(:version e) e]) (bhcore/?version registry versions))))
     joinedElements (mapv (fn [e] {:element e :version (get joined (:version e))}) elements)
     ]
    joinedElements
    )
  )

(defn buildCassandraRegistry [clusterHosts keyspace]
  (try
    (let
      [
       session (cassandra/buildSession clusterHosts)
       valuesTable :reg_values
       latestVarValuesTable :reg_lvn
       latestThreadValuesTable :reg_ltv
       varsTable :reg_vars
       threadsTable :reg_threads
       ]
      (log/info "creating keyspace and table")
      (cassandra/createKeyspace
        keyspace
        session)
      (cassandra/switchKeyspace
        keyspace
        session)

      (cassandra/createTable
        valuesTable
        {:version     :timeuuid
         :value       :varchar
         :primary-key [:version]}
        session
        )

      (cassandra/createTable
        latestVarValuesTable
        {
         :name        :varchar
         :thread      :varchar
         :version     :timeuuid
         :primary-key [:name :version]}
        {:clustering-order [[:version :desc]]}
        session
        )
      (cassandra/createTable
        latestThreadValuesTable
        {
         :name        :varchar
         :thread      :varchar
         :version     :timeuuid
         :primary-key [:thread :version]}
        {:clustering-order [[:version :desc]]}
        session
        )
      (cassandra/createTable
        varsTable
        {:name        :varchar
         :primary-key [[:name]]}
        session
        )
      (cassandra/createTable
        threadsTable
        {:thread      :varchar
         :primary-key [[:thread]]}
        session
        )

      (reify
        DefinitionsRegistry
        (+version [this name sessId value]
          (let
            [timeuuid (UUIDs/timeBased)]
            (cassandra/exeWithStatementLog
              session
              (stmt/batch
                (cls/queries
                  (stmt/insert
                    valuesTable
                    (cls/values {:version timeuuid
                                 :value   value})
                    )
                  (stmt/insert
                    latestVarValuesTable
                    (cls/values {:version timeuuid
                                 :name    name
                                 :thread  sessId})
                    )
                  (stmt/insert
                    latestThreadValuesTable
                    (cls/values {:version timeuuid
                                 :name    name
                                 :thread  sessId})
                    )
                  (stmt/insert
                    varsTable
                    (cls/values {:name name})
                    )
                  (stmt/insert
                    threadsTable
                    (cls/values {:thread sessId})
                    )
                  )
                )
              )
            timeuuid
            )
          )
        (?latest [this vars threads afterVersion beforeVersion limit]
          "latest version of name"
          (let
            [
             table (if (nil? vars) latestThreadValuesTable latestVarValuesTable)
             conditions
             [
              (if (nil? vars) nil [:in :name vars])
              (if (nil? threads) nil [:in :thread threads])
              (if (nil? afterVersion) nil [> :version afterVersion])
              (if (nil? beforeVersion) nil [< :version beforeVersion])]
             finalConditions (vec (filter some? conditions))
             ]
            (if (empty? finalConditions)
              (joinWithVersion
                this
                (cassandra/exeWithStatementLog
                  session
                  (stmt/select
                    table
                    (cls/order-by [:version :desc])
                    (cls/limit limit)
                    )
                  )
                )
              (joinWithVersion
                this
                (cassandra/exeWithStatementLog
                  session
                  (stmt/select
                    table
                    (cls/where finalConditions)
                    (cls/order-by [:version :desc])
                    (cls/limit limit)
                    (cls/allow-filtering true)
                    )
                  )
                )
              )
            )
          )
        (?earliest [this vars threads afterVersion beforeVersion limit]
          "earliest version of name at threads after specified version"
          (let
            [
             table (if (nil? vars) latestThreadValuesTable latestVarValuesTable)
             conditions
             [
              (if (nil? vars) nil [:in :name vars])
              (if (nil? threads) nil [:in :thread threads])
              (if (nil? afterVersion) nil [> :version afterVersion])
              (if (nil? beforeVersion) nil [< :version beforeVersion])]
             finalConditions (vec (filter some? conditions))
             ]
            (if (empty? finalConditions)
              (joinWithVersion
                this
                (cassandra/exeWithStatementLog
                  session
                  (stmt/select
                    table
                    (cls/order-by [:version :asc])
                    (cls/limit limit)
                    )
                  )
                )
              (joinWithVersion
                this
                (cassandra/exeWithStatementLog
                  session
                  (stmt/select
                    table
                    (cls/where finalConditions)
                    (cls/order-by [:version :asc])
                    (cls/limit limit)
                    (cls/allow-filtering true)
                    )
                  )
                )
              )
            )
          )
        (?version [this versions]
          "concrete version"
          (cassandra/exeWithStatementLog
            session
            (stmt/select
              valuesTable
              (cls/where [[:in :version (vec versions)]])
              )
            )
          )
        (?names
          [this from to]
          (let
            [
             fromConstraint (some-> from ((fn [x] [>= :name x])))
             toConstraint (some-> to ((fn [x] [< :name x])))
             rangeConstraint (vec (filter some? [fromConstraint toConstraint]))
             ]
            (cassandra/exeWithStatementLog
              session
              (stmt/select
                varsTable
                (cls/where rangeConstraint)
                (cls/allow-filtering true)
                )
              )
            )
          )

        java.io.Closeable
        (close [this]
          (try
            (some-> session (.close))
            (catch Throwable t
              (log/error t "unable to close cassandra session for registry")
              )
            )
          )

        )
      )
    (catch
      Throwable t
      (log/error t "unable to create cassandra registry")
      nil
      )
    )
  )

