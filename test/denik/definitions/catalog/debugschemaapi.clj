(ns denik.definitions.catalog.debugschemaapi
  (:require
    ;[denik.definitions.catalog.schemaapi :refer :all]
    [clojure.tools.logging :as log]
    ))

(require (quote [denik.definitions.catalog.schemaapi :refer :all]))

(log/info

  (->
    (createSchema "Event" [])
    (addField "f1" "string")
    (addField "f2" "int")
    )

  )
