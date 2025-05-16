(ns app.handlers
  (:require [re-frame.core :as rf]
            [cljs.reader :as reader]
            [cljs.pprint :as pprint]
            [app.db :as app.db]))

;; Effect handler that parses code-string to mvsj whenever code-string changes
(rf/reg-event-fx
 :code-string/changed
 (fn [{:keys [db]} [_ instance-id new-value]]
   {:db (app.db/update-instance-db db instance-id assoc :code-string new-value)
    :dispatch [:code/parse-to-mvsj instance-id]}))

(rf/reg-event-fx
 :app/init-db []
 (fn [_ [_ default-db]] {:db default-db}))

(rf/reg-event-fx
 :app/init-instance
 (fn [{:keys [db]} [_ instance-id initial-state]]
   (let [instance-db (if (string? initial-state)
                       {:code-string initial-state}
                       {:mvsj initial-state})]
     {:db (assoc-in db [:instances instance-id] instance-db)
      :dispatch (when (string? initial-state) [:code/parse-to-mvsj instance-id])})))

(rf/reg-event-db
 :mvsj/set
 (fn [db [_ instance-id mvsj-data]] 
   (app.db/update-instance-db db instance-id assoc :mvsj mvsj-data)))

(rf/reg-event-fx
 :app/show-notification
 (fn [_ [_ notification-data]]
   {:notification notification-data}))

(rf/reg-event-fx
 :code/set-string
 (fn [_ [_ instance-id code-string]]
   {:dispatch [:code-string/changed instance-id code-string]}))

(rf/reg-event-fx
 :code/set-initial
 (fn [_ [_ instance-id code]]
   (let [code-string (with-out-str (pprint/pprint code))]
     {:dispatch [:code-string/changed instance-id code-string]})))

(defn- nodize
  "Transforms a hiccup-like vector [:name props children] into a node map."
  [form]
  (let [[name-val props-val children-val] form]
    (cond-> {"kind" name-val}
      (seq props-val) (assoc "params" props-val)
      (seq children-val) (assoc "children" (mapv nodize children-val)))))

(defn create-mvsj [clj-code-string]
  (let [clj-data (try (reader/read-string clj-code-string)
                      (catch js/Error err
                        (js/console.error "Error parsing code:" err)
                        nil))]
    (when clj-data
      (let [msvj-state (nodize clj-data)]
        {:kind "single"
         :root msvj-state
         :metadata {:version "1.4"
                    :timestamp (.toISOString (js/Date.))}}))))

(rf/reg-event-fx
 :code/parse-to-mvsj
 (fn [{:keys [db]} [_ instance-id]]
   (let [instance-db (app.db/get-instance-db db instance-id)
         code-string (:code-string instance-db)
         mvsj-data (create-mvsj code-string)]
     (if mvsj-data
       {:db (app.db/update-instance-db db instance-id assoc :mvsj mvsj-data)
        :fx [[:dispatch [:app/show-notification {:type :success
                                               :message "Code parsed successfully"}]]]}
       {:fx [[:dispatch [:app/show-notification {:type :error
                                              :message "Failed to parse code"}]]]}))))
