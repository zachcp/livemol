(ns app.subs
  (:require [re-frame.core :as rf]
            [app.handlers :as handlers]))

(rf/reg-sub
 :app/mvsj
 (fn [db _] 
   (or (:mvsj db) 
       ;; If mvsj is not in the db, compute it on the fly
       (when-let [code-string (:code-string db)]
         (handlers/create-mvsj code-string)))))

(rf/reg-sub
 :app/code-string
 (fn [db _] (:code-string db)))
