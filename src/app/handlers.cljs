(ns app.handlers
  (:require [re-frame.core :as rf]
            [cljs.reader :as reader]
            [cljs.pprint :as pprint]))

(rf/reg-event-fx
 :app/init-db []
 (fn [_ [_ default-db]] {:db default-db}))

(rf/reg-event-db
 :mvsj/set
 (fn [db [_ mvsj-data]] (assoc db :mvsj mvsj-data)))

(rf/reg-event-fx
 :app/show-notification
 (fn [_ [_ notification-data]]
   {:notification notification-data}))

(rf/reg-event-db
 :code/set-string
 (fn [db [_ code-string]]
   (assoc db :code-string code-string)))

(rf/reg-event-db
 :code/set-initial
 (fn [db [_ initial-code]]
   (let [code-string (with-out-str (pprint/pprint initial-code))]
     (-> db
         (assoc :initial-code initial-code)
         (assoc :code-string code-string)))))

(defn- nodize
  "Transforms a hiccup-like vector [:name props children] into a node map."
  [form]
  (let [[name-val props-val children-val] form]
    (cond-> {"kind" name-val}
      (not (empty? props-val)) (assoc "params" props-val)
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
 (fn [{:keys [db]} [_]]
   (let [code-string (:code-string db)
         mvsj-data (create-mvsj code-string)]
     (if mvsj-data
       {:db (assoc db :mvsj mvsj-data)
        :fx [[:dispatch [:app/show-notification {:type :success
                                               :message "Code parsed successfully"}]]]}
       {:fx [[:dispatch [:app/show-notification {:type :error
                                              :message "Failed to parse code"}]]]}))))
