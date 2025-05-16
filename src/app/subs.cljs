(ns app.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :app/mvsj
 (fn [db _] (:mvsj db)))
