(ns denik.definitions.catalog.readers
  (:require [clojure.tools.logging :as log])
  )

(defn regRefReader [form]
  (log/info form)
  form
  )