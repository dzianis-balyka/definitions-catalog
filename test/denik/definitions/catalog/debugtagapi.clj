(ns denik.definitions.catalog.debugtagapi
  (:require
    [denik.definitions.catalog.tagsapi :as tapi]
    [denik.definitions.catalog.exectx :as exectx]
    [clojure.tools.logging :as log]
    )
  )

(def testTag "v1.0")

;(defn tst [c]
;  `(intern (symbol "xxx") (into (get (ns-interns *ns*) (symbol "xxx")) ~c))
;  )
;
;(log/info (str (tapi/tagMut {"a" 1 "b" 2})))
;;(log/info (macroexpand '(tst {"a" 1 "b" 2})))
;
;(log/info
;  (into
;    (into
;      '({"a" "b"})
;      '(into values x)
;      )
;    '(def values)
;    )
;  )

(defn tagJournal []
  [
   {:id  nil :ts nil :chain nil :place nil :thread nil
    :edn (exectx/ednStr
           (when (not (contains? (ns-interns *ns*) (symbol denik.definitions.catalog.tagsapi/tagValuesVarName)))
             (intern *ns* (symbol denik.definitions.catalog.tagsapi/tagValuesVarName) {})
             )
           )
    }
   {:id  nil :ts nil :chain nil :place nil :thread nil
    :edn (str (tapi/tagMut {"p" "v"}))
    }
   ])

(let
  [chain (tagJournal)
   states (exectx/states chain (str testTag (System/currentTimeMillis)))]
  (mapv #(log/info "!!!" %1 %2) chain states)
  )

