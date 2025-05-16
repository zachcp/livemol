(ns app.core
  (:require
   [app.subs]
   [app.handlers]
   [app.fx]
   [app.db]
   [app.components.molstar :refer [MolstarMVS]]
   [app.components.codemirror :refer [CodeMirrorCLJ]]
   [re-frame.core :as rf]
   [uix.core :as uix :refer [defui $]]
   [uix.dom]
   [cljs.reader :as reader]))


(defui app []
  ($ :div.wrapper
     ($ CodeMirrorCLJ)
     ($ MolstarMVS)))

(defn create-app-root
  "Creates a UIx root from a DOM element with the given ID"
  [root-id]
  (uix.dom/create-root (js/document.getElementById root-id)))

(defn parse-initial-state [state-str]
  (when (and state-str (seq state-str))
    (try
      (let [parsed (reader/read-string state-str)]
        (if (map? parsed) parsed nil))
      (catch :default _
        nil))))

(defn render
  "Renders the application into the provided root"
  [root initial-state]
  (let [db (if initial-state
             (assoc app.db/default-db :mvsj initial-state)
             app.db/default-db)]
    (rf/dispatch-sync [:app/init-db db])
    ;; Parse the initial code-string to MVSJ
    (rf/dispatch [:code/parse-to-mvsj])
    (uix.dom/render-root
     ($ uix/strict-mode ($ app))
     root)))

(defn ^:export set-data
  "Updates the MVSJ data in an already initialized component.

   Call this function after init to update the molecule data:

   <script>
     // First initialize the component
     app.core.init('custom-root-id');

     // Then you can update it with new data
     app.core.set_data(customDataObject);

     // Or pass a ClojureScript data structure as a string
     app.core.set_data('{:kind \"single\" :root {...}}');
   </script>"
  [data]
  (let [parsed-data (if (string? data) (parse-initial-state data) data)]
    (when parsed-data
      (if (and (map? parsed-data) (contains? parsed-data :kind))
        ;; If it's already in MVSJ format, set it directly
        (rf/dispatch [:mvsj/set parsed-data])
        ;; Otherwise, convert it to a code string first
        (rf/dispatch [:code/set-initial parsed-data])))))

(defn ^:export init
  "Initialize the application.

   When called with no arguments, uses 'root' as the default element ID.
   When called with a root-id argument, mounts the app to that element.
   When called with a root-id and initial-state, mounts the app and populates
   the :mvsj data in the re-frame database.

   This function needs to be explicitly called in your HTML:

   <script>
     // Basic initialization
     app.core.init('custom-root-id');

     // Initialize with custom state data (JavaScript object)
     var initialState = {...}; // Your MVS data object
     app.core.init('custom-root-id', initialState);

     // Initialize with ClojureScript data as string
     var stateString = '{:kind \"single\" :root {...}}';
     app.core.init('custom-root-id', stateString);

     // You can also retrieve initial state from a data attribute
     var content = document.getElementById('custom-root-id').getAttribute('data-content');
     app.core.init('custom-root-id', content);
   </script>"
  ([] (init "root"))
  ([root-id] (init root-id nil))
  ([root-id initial-state]
   (let [root (create-app-root root-id)
         parsed-state (if (string? initial-state)
                        (parse-initial-state initial-state)
                        initial-state)]
     (render root parsed-state))))
