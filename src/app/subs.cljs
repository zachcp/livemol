(ns app.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :app/mvsj
 (fn [db _] (:mvsj db)))

(rf/reg-sub
 :app/initial-code
 (fn [db _] (:initial-code db)))

(rf/reg-sub
 :app/code-string
 (fn [db _] (:code-string db)))
