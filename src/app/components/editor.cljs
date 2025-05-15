(ns app.components.editor
  (:require
   [uix.core :as uix :refer [defui $]]
   [uix.dom]
   ["prismjs" :as Prism]
   ["prismjs/components/prism-clojure" :as prism-clojure]
   ["codejar" :refer [CodeJar]]
   [clojure.string :as str]))

(defui code-editor [{:keys [value on-change options class-name]}]
  (let [editor-ref (uix/use-ref nil)
        jar-ref (uix/use-ref nil)
        handle-change (fn [new-code] (when on-change (on-change new-code)))
        options (merge {:tab "  "
                        :spellcheck false
                        :catchTab true
                        :preserveIdent true
                        :addClosing true}
                       options)]

    (uix/use-effect
     (fn []
       (when (and editor-ref (.-current editor-ref) (not @jar-ref))
         (let [highlight (fn [element] (Prism/highlightElement element))
               jar (CodeJar. (.-current editor-ref) highlight (clj->js options))]
           (.onUpdate jar handle-change)
           (.updateCode ^js jar (or value ""))
           (reset! jar-ref jar))))
     [])

    (uix/use-effect
     (fn []
       (when (and @jar-ref value)
         (let [jar @jar-ref
               current-code (.toString jar)]
           (when (not= current-code value)
             (.updateCode ^js jar value)))))
     [value])

    ;; (uix/use-cleanup
    ;;  (fn []
    ;;    (when @jar-ref
    ;;      (.destroy @jar-ref))))

    ($ :div {:class (str "clojure-editor-wrapper " (or class-name ""))}
       ($ :pre {:class "language-clojure editor-container"
                :ref editor-ref}
          ($ :code {:class "language-clojure"} value)))))

(defui clojure-editor [{:keys [value on-change placeholder read-only
                               class-name height max-height min-height
                               auto-focus]}]
  (let [editor-options {:readonly read-only
                        :placeholder placeholder
                        :catchTab true
                        :preserveIdent true}
        style (cond-> {}
                height (assoc :height height)
                max-height (assoc :maxHeight max-height)
                min-height (assoc :minHeight min-height))]

    ($ code-editor
       {:value value
        :on-change on-change
        :options editor-options
        :class-name class-name
        :style style})))

;; Export for use
(def ClojureEditor clojure-editor)
