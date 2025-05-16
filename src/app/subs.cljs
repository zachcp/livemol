(ns app.subs
  (:require [re-frame.core :as rf]
            [app.handlers :as handlers]
            [app.db :as db]))

;; Instance-aware subscriptions
(rf/reg-sub
 :app/instance-db
 (fn [db [_ instance-id]]
   (db/get-instance-db db instance-id)))

(rf/reg-sub
 :app/mvsj
 (fn [[_ instance-id]]
   (rf/subscribe [:app/instance-db instance-id]))
 (fn [instance-db _] 
   (or (:mvsj instance-db) 
       ;; If mvsj is not in the instance-db, compute it on the fly
       (when-let [code-string (:code-string instance-db)]
         (handlers/create-mvsj code-string)))))

(rf/reg-sub
 :app/code-string
 (fn [[_ instance-id]]
   (rf/subscribe [:app/instance-db instance-id]))
 (fn [instance-db _] 
   (let [code-string (:code-string instance-db)]
     (if (string? code-string)
       code-string
       (or (and code-string (str code-string)) app.db/default-code-string)))))
