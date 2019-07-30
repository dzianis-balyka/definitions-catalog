(ns denik.definitions.catalog.ast
  (:require [clojure.java.io :as io])
  (:import (java.nio.charset StandardCharsets)
           (java.io PushbackReader)))


(defn toAst [s]
  (let
    [
     pbr (PushbackReader. (io/reader (.getBytes s StandardCharsets/UTF_8)))
     stmtsSeq (take-while #(nil? (second %)) (repeatedly #(try [(read pbr) nil] (catch Throwable t [nil t]))))
     stmts (map #(first %) stmtsSeq)
     ]
    (vec stmts)
    )
  )