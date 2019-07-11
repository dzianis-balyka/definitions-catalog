(defproject gspaces "0.0.1-SNAPSHOT"
  :description "experiments with spaces"
  :url "http://TODO"
  :license {:name "private"
            :url  "private"}
  :dependencies [
                 [org.clojure/clojure "1.10.0"]
                 [ubergraph "0.5.1"]
                 [clojurewerkz/ogre "3.3.3.0"]
                 [org.janusgraph/janusgraph-core "0.3.1" :exclusions [ch.qos.logback/logback-classic]]
                 [org.janusgraph/janusgraph-cassandra "0.3.1" :exclusions [ch.qos.logback/logback-classic]]
                 [org.janusgraph/janusgraph-cql "0.3.1" :exclusions [ch.qos.logback/logback-classic]]
                 [org.janusgraph/janusgraph-es "0.3.1" :exclusions [ch.qos.logback/logback-classic]]
                 [org.apache.tinkerpop/gremlin-server "3.3.3" :exclusions [ch.qos.logback/logback-classic]]
                 [cc.qbits/alia-all "4.3.0"]
                 [cc.qbits/hayt "4.0.2"]
                 [org.slf4j/slf4j-log4j12 "1.7.25"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [org.rocksdb/rocksdbjni "5.17.2"]
                 [org.clojure/tools.logging "0.4.1"]
                 [vvvvalvalval/scope-capture "0.3.2"]

                 [com.wjoel/clj-bean "0.2.1"]

                 [javax.cache/cache-api "1.0.0"]

                 [org.apache.ignite/ignite-slf4j "2.7.5"]
                 [org.apache.ignite/ignite-core "2.7.5"]
                 [org.apache.ignite/ignite-indexing "2.7.5"]
                 [org.apache.ignite/ignite-spring "2.7.5"]

                 [com.bhauman/figwheel-main "0.2.1"]

                 ]
  ;:main
  :aot [denik.definitions.catalog.core denik.definitions.catalog.schemaapi]
  )
