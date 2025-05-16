(ns app.components.codemirror
  (:require
   [uix.core :as uix :refer [defui $]]
   [uix.dom]
   [re-frame.core :as rf]
   [uix.re-frame :as urf]
   [clojure.string :as str]
   ["@uiw/react-codemirror$default" :as CodeMirror]
   ["@nextjournal/clojure-mode" :refer [default_extensions, complete_keymap]]
   ["@codemirror/view" :refer [keymap]]))

(defn- format-code-structure
  ([node] (format-code-structure node 0))
  ([node indent-level]
   (let [indent-str (apply str (repeat indent-level "  "))]
     (cond
       (vector? node)
       (if (and (>= (count node) 2) (keyword? (first node)) (map? (second node)))
         (let [[tag props & children] node]
           (str indent-str "[" tag " " (pr-str props)
                (if (seq children)
                  (str "\n" (str/join "\n" (map #(format-code-structure % (inc indent-level)) (first children))) "\n" indent-str "]")
                  " []]")))
         (str/join "\n" (map #(format-code-structure % indent-level) node)))

       :else
       (str indent-str (pr-str node))))))

(defui CodeMirrorCLJ [{:keys [instance-id]}]
  (let [code-string (urf/use-subscribe [:app/code-string instance-id])
        onchange (uix/use-callback
                  (fn [val _viewupdate]
                    (rf/dispatch [:code-string/changed instance-id val]))
                  [instance-id])]
    ($ :div.codemirror_container
       ($ CodeMirror
          {:value code-string
           :height "250px"
           :extensions
           #js [(.of keymap complete_keymap)
                (.of keymap #js {:key "Alt-Enter"
                                 :run  (fn [e]
                                         (let [editor-text (.toString (.. e -state -doc))]
                                           (rf/dispatch [:code-string/changed instance-id editor-text])
                                           true))})
                default_extensions]
           :on-change onchange}))))
