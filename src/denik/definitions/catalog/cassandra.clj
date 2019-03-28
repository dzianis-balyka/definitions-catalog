(ns denik.definitions.catalog.cassandra
  (:require [qbits.alia :as alia]
            [qbits.hayt :as hayt]
            [qbits.hayt.dsl.statement :as stmt]
            [qbits.hayt.dsl.clause :as cls]
            [clojure.tools.logging :as log])
  )


(defn buildCluster [hosts]
  (alia/cluster {:contact-points hosts})
  )

(defn buildSession [hosts]
  (alia/connect (alia/cluster {:contact-points hosts}))
  )

(defn doWithSession
  ([func cluster]
   (try
     (with-open [sess (alia/connect cluster)]
       (func sess))
     (catch Throwable t (.printStackTrace t))))
  ([func]
   (doWithSession func (buildCluster ["localhost"]))
    )
  )

(defn exeWithStatementLog [session stmt]
  (log/info (hayt/->raw stmt))
  (alia/execute session stmt)
  )


(defn createKeyspace [keySpace session]
  (exeWithStatementLog
    session
    (stmt/create-keyspace
      keySpace
      (cls/if-exists false)
      (cls/with {:replication
                 {:class              "SimpleStrategy"
                  :replication_factor 1}}))
    )
  )

(defn createTable
  ([table tableColumns session]
   (exeWithStatementLog
     session
     (stmt/create-table
       table
       (cls/if-exists false)
       (cls/column-definitions tableColumns))
     )
    )
  ([table tableColumns tableOptions session]
   (exeWithStatementLog
     session
     (stmt/create-table
       table
       (cls/if-exists false)
       (cls/column-definitions tableColumns)
       (cls/with tableOptions))
     )
    )
  )
(defn createIndex [table idxName tableColumns session]
  (exeWithStatementLog
    session
    (stmt/create-index
      table
      tableColumns
      (cls/index-name idxName)
      (cls/if-exists false)
      )
    )
  )
(defn switchKeyspace [keySpace session]
  (exeWithStatementLog session (stmt/use-keyspace keySpace))
  )
(defn createKeyspaceAndSchema [keySpace table tableColumns tableOptions session]
  (createKeyspace keySpace session)
  (switchKeyspace keySpace session)
  (createTable table tableColumns tableOptions session)
  )

