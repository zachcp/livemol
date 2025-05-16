(ns app.core
  (:require
   [app.subs]
   [app.handlers]
   [app.fx]
   [app.db]
   [app.components.molstar :refer [MolstarMVS]]
   [app.components.codemirror :refer [CodeMirrorCLJ]]
   [cljs.reader :as reader]
   [re-frame.core :as rf]
   [uix.core :as uix :refer [defui $]]
   [uix.dom]))

(defui app []
  ($ :div.wrapper
     ($ CodeMirrorCLJ)
     ($ MolstarMVS)))

(defn render
  "Renders the application into the provided root"
  [root initial-state]
  (let [db (cond
             (string? initial-state) (assoc app.db/default-db :code-string initial-state)
             (map? initial-state) (assoc app.db/default-db :mvsj initial-state)
             :else app.db/default-db)]
    (rf/dispatch-sync [:app/init-db db])
    ;; Parse the initial code-string to MVSJ if we have code-string
    (when (string? initial-state)
      (rf/dispatch [:code/parse-to-mvsj]))
    (uix.dom/render-root
     ($ uix/strict-mode ($ app))
     root)))

(defn parse-initial-state
  "Tries to parse initial state from a string.
   If the string looks like a code representation, returns it as-is.
   If it looks like MVSJ JSON data, parses it to a ClojureScript map.
   Handles HTML-escaped characters in the string."
  [state-string]
  (let [unescaped-string (-> state-string
                            (.replace (js/RegExp. "&quot;" "g") "\"")
                            (.replace (js/RegExp. "&amp;" "g") "&")
                            (.replace (js/RegExp. "&lt;" "g") "<")
                            (.replace (js/RegExp. "&gt;" "g") ">"))]
    (try
      (let [parsed (reader/read-string unescaped-string)]
        (if (vector? parsed)
          ;; It's code representation
          unescaped-string
          ;; It might be JSON-formatted MVSJ data
          (js->clj (js/JSON.parse unescaped-string))))
      (catch js/Error _
        (try
          ;; Try to parse as JSON if ClojureScript reader failed
          (js->clj (js/JSON.parse unescaped-string))
          (catch js/Error _
            ;; Return as-is if all parsing attempts fail
            unescaped-string))))))

(defn ^:export init
  "Initialize the application.

   When called with no arguments, uses 'root' as the default element ID.
   When called with a root-id argument, mounts the app to that element.
   When called with a root-id and initial-state, mounts the app and populates
   the database with appropriate data.

   This function needs to be explicitly called in your HTML:

   <script>
     // Basic initialization
     app.core.init('custom-root-id');

     // Initialize with code string (ClojureScript data as string)
     var codeString = '[:root {} [[:download {...} [...]]]]';
     app.core.init('custom-root-id', codeString);

     // Initialize with MVSJ data (JavaScript object)
     var mvsj = {kind: 'single', root: {...}};
     app.core.init('custom-root-id', mvsj);

     // Initialize with MVSJ as JSON string
     var mvsjString = '{\"kind\":\"single\",\"root\":{...}}';
     app.core.init('custom-root-id', mvsjString);

     // You can also retrieve initial state from a data attribute
     var content = document.getElementById('custom-root-id').getAttribute('data-content');
     app.core.init('custom-root-id', content);
   </script>"
  ([] (init "root"))
  ([root-id] (init root-id nil))
  ([root-id initial-state]
   (let [root (uix.dom/create-root (js/document.getElementById root-id))
         processed-state (cond
                           ;; If it's a string, try to parse it appropriately
                           (string? initial-state) (parse-initial-state initial-state)
                           ;; If it's a JavaScript object, convert to ClojureScript map
                           (and initial-state (not (map? initial-state))) (js->clj initial-state)
                           ;; Otherwise pass through
                           :else initial-state)]
     (render root processed-state))))
