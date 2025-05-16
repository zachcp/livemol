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
   [uix.dom]))

(defui header []
  ($ :header.app-header
     ($ :img {:src "https://raw.githubusercontent.com/pitch-io/uix/master/logo.png"
              :width 32})))

(defui footer []
  ($ :footer.app-footer
     ($ :small "made with "
        ($ :a {:href "https://github.com/pitch-io/uix"}
           "UIx"))))

(defui app []
  ($ :div.wrapper
     ($ CodeMirrorCLJ)
     ($ MolstarMVS)))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn render []
  (rf/dispatch-sync [:app/init-db app.db/default-db])
  (uix.dom/render-root
   ($ uix/strict-mode ($ app))
   root))

(defn ^:export init []
  (render))
