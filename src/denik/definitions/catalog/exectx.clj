(ns denik.definitions.catalog.exectx
  (:require [clojure.tools.logging :as log]
            [denik.definitions.catalog.core :as dbc]
            [clojure.string :as str])
  (:import (java.util Iterator)))

(declare ^:dynamic registry)
(def ^:dynamic invocationStack [])
(def ^:dynamic depStacks [])
(def ^:dynamic placesStack [])

(defn fromReg [name version variable]

  (if (some #(= [name version variable] %) invocationStack)
    (do
      (log/warn "dependency circle detected " invocationStack)
      (throw (RuntimeException. (str "dependency circle detected " invocationStack)))
      )
    (binding
      [invocationStack (conj invocationStack [name version variable])]
      ;(if
      ;  (and
      ;    (not (nil? (find-ns (symbol (dbc/replsns name version)))))
      ;    )
      ;  )
      (let
        [
         nameVersion (first (dbc/?version registry [version]))
         ast (some-> nameVersion (:value) (dbc/toAst))
         evalResult (some-> ast (#(dbc/evalnv name version %)))

         replInterns (ns-interns (symbol (dbc/replsns name version)))
         varsInterns (ns-interns (symbol (dbc/varsns name version)))
         resultsVar (some-> replInterns (get (symbol "results")))
         results (some-> resultsVar (var-get))
         ]
        (log/info "ast" ast)
        ;(log/info "!!!er" evalResult)
        ;(log/info "!!!is" invocationStack)
        (if (nil? variable)
          (some-> results (last))
          (some-> varsInterns (get (symbol variable)) (var-get))
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


(defn latest
  ([places chains threads to limit]
   (let
     [
      element (first (dbc/?< registry places chains threads nil to limit))
      ]
     element
     )
    )
  )

(defn earliest
  ([places chains threads from limit]
   (let
     [
      element (first (dbc/?< registry places chains threads from nil limit))
      ]
     element
     )
    )
  )

(defn versions
  ([versions]
   (let
     [
      element (first (dbc/?v registry versions))
      ]
     element
     )
    )
  )



(defn evalElement [element]
  (binding
    [
     placesStack (conj placesStack element)
     ]
    (let
      [
       ast (dbc/toAst (:edn element))
       evalResult (dbc/evalnv (:place element) (:version element) ast)
       ]
      (log/info element)
      (log/info ast)
      (log/info evalResult)
      evalResult
      )
    )
  )

(defn chainToSeq [places chains threads from to batchSize]
  (let
    [
     currentBatch (ref (dbc/?> registry places chains threads from to batchSize))
     iterator (reify Iterator
                (Iterator/next [it]
                  (dosync
                    (let
                      [
                       res (first @currentBatch)
                       restElements (rest @currentBatch)
                       ]
                      (ref-set
                        currentBatch
                        (if
                          (empty? restElements)
                          (filter (fn [x] (not (= (:id x) (:id res)))) (dbc/?> registry places chains threads (:ts res) to batchSize))
                          restElements
                          )
                        )
                      res
                      )
                    )
                  )
                (Iterator/hasNext [it]
                  (dosync
                    (not (empty? @currentBatch))
                    )
                  )
                )
     ]
    (iterator-seq iterator)
    )
  )

(defn evalInNs [namespaceName asts]
  (let
    [
     previousNs *ns*
     varsNsSymbol (symbol namespaceName)
     replResults (do
                   (in-ns varsNsSymbol)
                   (refer 'clojure.core)
                   (mapv
                     #(try
                        (eval %)
                        (catch Throwable t
                          (log/error t "error during eval of" % "at" namespaceName)
                          nil
                          ))
                     asts
                     )
                   )
     ]
    (in-ns (ns-name previousNs))
    replResults
    )
  )

(defmacro ednStr [& form]
  (str/join (mapv #(str %) form))
  )

(defn states [chain space]
  (let
    [
     asts (map #(dbc/toAst (:edn %)) chain)
     ]
    (map
      (fn
        [e]
        (evalInNs space e)
        (some->
          (ns-interns (symbol space))
          ((fn [x] (if (empty? x) {} (into (hash-map) (mapv (fn [y] [(first y) @(last y)]) x)))))
          )
        )
      asts
      )
    )
  )