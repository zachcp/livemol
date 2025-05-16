(ns app.fx
  (:require [re-frame.core :as rf]))

(rf/reg-fx
 :notification
 (fn [{:keys [type message]}]
   ;; You can implement your notification UI logic here
   (case type
     :success (js/console.log "SUCCESS:" message)
     :error (js/console.error "ERROR:" message)
     :warning (js/console.warn "WARNING:" message)
     (js/console.log "INFO:" message))))
