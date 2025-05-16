(ns app.handlers
  (:require [re-frame.core :as rf]))

(rf/reg-event-fx
 :app/init-db []
 (fn [_ [_ default-db]] {:db default-db}))

(rf/reg-event-db
 :mvsj/set
 (fn [db [_ mvsj-data]] (assoc db :mvsj mvsj-data)))
