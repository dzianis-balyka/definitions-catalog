(ns denik.definitions.catalog.exectx
  (:require [clojure.tools.logging :as log]
            [denik.definitions.catalog.core :as dbc])
  )

(declare ^:dynamic registry)
(def ^:dynamic invocationStack [])
(def ^:dynamic depStacks [])

(defn fromReg [n v p]

  (if (some #(= [n v p] %) invocationStack)
    (do
      (log/warn "dependency circle detected " invocationStack)
      (throw (RuntimeException. (str "dependency circle detected " invocationStack)))
      )
    (binding
      [invocationStack (conj invocationStack [n v p])]
      ;(if
      ;  (and
      ;    (not (nil? (find-ns (symbol (dbc/replsns n v)))))
      ;    )
      ;  )
      (let
        [
         nameVersion (first (dbc/?version registry [v]))
         ast (some-> nameVersion (:value) (dbc/toAst))
         evalResult (some-> ast (#(dbc/evalnv n v %)))

         replInterns (ns-interns (symbol (dbc/replsns n v)))
         varsInterns (ns-interns (symbol (dbc/varsns n v)))
         resultsVar (some-> replInterns (get (symbol "results")))
         results (some-> resultsVar (var-get))
         ]
        (log/info "ast" ast)
        ;(log/info "!!!er" evalResult)
        ;(log/info "!!!is" invocationStack)
        (if (nil? p)
          (some-> results (last))
          (some-> varsInterns (get (symbol p)) (var-get))
          )
        ))
    )
  )
;(defn fromRegLatest [n ts p]
;
;  (if (some #(= [n v p] %) invocationStack)
;    (do
;      (log/warn "dependency circle detected " invocationStack)
;      (throw (RuntimeException. (str "dependency circle detected " invocationStack)))
;      )
;    (binding
;      [invocationStack (conj invocationStack [n v p])]
;      ;(if
;      ;  (and
;      ;    (not (nil? (find-ns (symbol (dbc/replsns n v)))))
;      ;    )
;      ;  )
;      (let
;        [
;         nameVersion (first (dbc/?version registry [v]))
;         ast (some-> nameVersion (:value) (dbc/toAst))
;         evalResult (some-> ast (#(dbc/evalnv n v %)))
;
;         replInterns (ns-interns (symbol (dbc/replsns n v)))
;         varsInterns (ns-interns (symbol (dbc/varsns n v)))
;         resultsVar (some-> replInterns (get (symbol "results")))
;         results (some-> resultsVar (var-get))
;         ]
;        ;(log/info "!!!er" evalResult)
;        ;(log/info "!!!is" invocationStack)
;        (if (nil? p)
;          (some-> results (last))
;          (some-> varsInterns (get (symbol p)) (var-get))
;          )
;        ))
;    )
;  )