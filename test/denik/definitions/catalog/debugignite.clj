(ns denik.definitions.catalog.debugignite
  (:require
    [denik.definitions.catalog.ignite :refer :all]
    [clojure.tools.logging :as log]
    )
  (:import (org.apache.ignite Ignite Ignition)
           (org.apache.ignite.configuration IgniteConfiguration)))


(defn doWithLocalCluster [f]
  (let
    [cfg (new IgniteConfiguration)]
    (with-open [ignite (Ignition/start "ignite.cfg.xml")]
      (f ignite)
      )
    )
  )


(doWithLocalCluster
  (fn [ign]
    ;(prn "!!!")
    (let [cache (.getOrCreateCache ign "Elements")
          idx (.cre cache)])

    (.getOrCreateCache ign "Elements")
    (log/info "!!!" ign))
  )