(ns app.db
  (:require [cljs.pprint :as pprint]))

(defn- capture-pprint [data]
  (with-out-str
    (pprint/pprint data)))

(def default-code
  [:root {}
   [[:download {:url "https://files.wwpdb.org/download/1cbs.cif"}
     [[:parse {:format "mmcif"}
       [[:structure {:type "model"}
         [[:component {:selector "all"}
           [[:representation {:type "cartoon"}
             [[:color {:color "blue"} nil]]]]]]]]]]]]])

(def default-code-string
  (capture-pprint default-code))

;; {initial state/ editor } -> :code-string -> :mvsj -> Molview Cis
(def default-db
  {:instances {}})

(defn get-instance-db
  "Get a specific instance's db state, creating it if it doesn't exist"
  [db instance-id]
  (get-in db [:instances instance-id] 
          {:code-string default-code-string
           :mvsj {}}))

(defn update-instance-db
  "Update a specific instance's db state"
  [db instance-id update-fn & args]
  (apply update-in db [:instances instance-id] update-fn args))
