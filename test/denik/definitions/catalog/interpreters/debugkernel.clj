(ns denik.definitions.catalog.interpreters.debugkernel
  (:require [denik.definitions.catalog.interpreters.kernel :refer :all]
            [clojure.tools.logging :as log]))

;(interpret '(a b c) defaultIntDict {})


(log/info (interpret [1 2 3] defaultIntDict {}))
(log/info (interpret #{1 2 3} defaultIntDict {}))
(log/info (interpret {1 2 3 4} defaultIntDict {}))


