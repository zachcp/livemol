(ns app.db
  (:require [cljs.pprint :as pprint]))

(defn- capture-pprint [data]
  (with-out-str
    (pprint/pprint data)))

(def initial-code
  [:root {}
   [[:download {:url "https://files.wwpdb.org/download/1cbs.cif"}
     [[:parse {:format "mmcif"}
       [[:structure {:type "model"}
         [[:component {:selector "all"}
           [[:representation {:type "cartoon"}
             [[:color {:color "blue"} nil]]]]]]]]]]]]])

(def initial-code-string
  (capture-pprint initial-code))

(def mvs-sample
  {:kind "single"
   :root {:kind "root"
          :children
          [{:kind "download"
            :params {:url "https://files.wwpdb.org/download/1cbs.cif"}
            :children
            [{:kind "parse"
              :params {:format "mmcif"}
              :children
              [{:kind "structure"
                :params {:type "model"}
                :children
                [{:kind "component"
                  :params {:selector "all"}
                  :children
                  [{:kind "representation"
                    :params {:type "cartoon"}
                    :children
                    [{:kind "color"
                      :params {:color "blue"}}]}]}]}]}]}]}
   :metadata
   {:timestamp "2025-04-14T19:04:58.549065+00:00"
    :version "1.4"}})

(def default-db
  {:mvsj mvs-sample
   :initial-code initial-code
   :code-string initial-code-string})
