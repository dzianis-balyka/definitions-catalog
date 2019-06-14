(ns denik.definitions.catalog.schemaapi)


(def schemasVarName "schemas")

(defn createSchema [name fields]
  {
   :name   name
   :fields fields
   }
  )

(defn addField [schema name type]
  (assoc
    schema
    :fields
    (conj
      (:fields schema) {:name name
                        :type type})
    )
  )

(defn removeField [schema name]
  (assoc schema :fields (vec (remove #(= name (:name %)) (:fields schema))))
  )
(defn getSchemas []
  (let
    [schemaVarMaybe (get (ns-interns *ns*) (symbol schemasVarName))
     schemaVar (if (nil? schemaVarMaybe) (intern *ns* (symbol schemasVarName) []) schemaVarMaybe)]
    (deref schemaVar)
    )
  )

(defn findSchema [name]
  (first (vec (filter #(= name (:name %)) (getSchemas))))
  )

(defn addSchema [schema]
  (intern *ns* (symbol schemasVarName) (conj (getSchemas) schema))
  )

(defn removeSchema [name]
  (intern *ns* (symbol schemasVarName) (vec (remove #(= name (:name %)) (getSchemas))))
  )


