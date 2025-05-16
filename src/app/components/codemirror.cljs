(ns app.components.codemirror
  (:require
   [uix.core :as uix :refer [defui $]]
   [uix.dom]
   [re-frame.core :as rf]
   [cljs.pprint :as pprint]
   [clojure.string :as str]
   [cljs.reader :as reader]
   ["@uiw/react-codemirror$default" :as CodeMirror]
   ["@nextjournal/clojure-mode" :refer [default_extensions, complete_keymap]]
   ["@codemirror/view" :refer [keymap]]))

(defn- format-code-structure
  ([node] (format-code-structure node 0))
  ([node indent-level]
   (if (vector? node)
     (if (and (keyword? (first node)) (map? (second node)))
       (let [[tag props children] node
             indent (apply str (repeat indent-level "  "))]
         (str indent "[" tag " " (pr-str props)
              (if (seq children)
                (str "\n" (format-code-structure children (inc indent-level)) "\n" indent "]")
                " []]"))
         (str/join "\n" (map #(format-code-structure % indent-level) node)))
       (str (apply str (repeat indent-level "  ")) "[]")))))

(defn- nodize
  "Transforms a hiccup-like vector [:name props children] into a node map. "
  [form]
  (let [[name-val props-val children-val] form]
    (cond-> {"kind" name-val}
      (not (empty? props-val)) (assoc "params" props-val)
      (seq children-val) (assoc "children" (mapv nodize children-val)))))

(defn- create-mvsj [clj-code-string]
  (let [clj-data (reader/read-string clj-code-string)
        nodize (fn [form] (let [[name-val props-val children-val] form]
                            (cond-> {"kind" name-val}
                              (not (empty? props-val)) (assoc "params" props-val)
                              (seq children-val) (assoc "children" (mapv nodize children-val)))))
        msvj-state (nodize clj-data)]
    {:kind "single"
     :root msvj-state
     :metadata {:version "1.4"
                :timestamp "2025-04-14T19:04:58.549065+00:00"}}))

(def initial-code
  [:root {}
   [[:download {:url "https://files.wwpdb.org/download/1cbs.cif"}
     [[:parse {:format "mmcif"}
       [[:structure {:type "model"}
         [[:component {:selector "all"}
           [[:representation {:type "cartoon"}
             [[:color {:color "blue"} nil]]]]]]]]]]]]])

(defn- capture-pprint [data]
  (with-out-str
    (pprint/pprint data)))

(defui CodeMirrorCLJ []
  (let [codestring (capture-pprint initial-code)
        [value set-value] (uix/use-state codestring)
        onchange (uix/use-callback (fn [val, viewupdate] (set-value val)) [])]
    ($ :div.codemirror_container
       ($ CodeMirror
          {:value value
           ;; :height "250px"
           :extensions
           #js [(.of keymap complete_keymap)
                (.of keymap #js {:key "Alt-Enter"
                                 :run  (fn [e]
                                         (let [editor-text (.toString (.. e -state -doc))
                                               results (try
                                                         (let [parsed-mvsj (create-mvsj editor-text)]
                                                           (js/console.log parsed-mvsj)
                                                           (rf/dispatch [:mvsj/set parsed-mvsj])
                                                           parsed-mvsj)
                                                         (catch js/Error err
                                                           (js/console.error "Error parsing MVSJ:" err)
                                                           nil))]
                                           (js/console.log "Editor contents:" editor-text)
                                           (when results
                                             (js/console.log "Parsed MVSJ:", results))
                                           true))})
                default_extensions]
           :on-change onchange}))))
