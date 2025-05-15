(ns app.components.molstar
  (:require
   [uix.core :as uix :refer [defui $]]
   [uix.dom]))

(def molstar-params
  #js {:allowMajorPerformanceCaveat true
       :collapseLeftPanel false
       :collapseRightPanel false
       :customFormats []
       :disableAntialiasing false
       :disabledExtensions []
       :emdbProvider "rcsb"
       ;;  :extensions []
       :illumination false
       ;;  :layoutControlsDisplay "reactive"
       :layoutIsExpanded false
       :layoutShowControls false
       :layoutShowLeftPanel false
       :layoutShowLog false
       :layoutShowRemoteState false
       :layoutShowSequence true
       :pdbProvider "rcsb"
       :pickScale 1
       :pixelScale 1
       :pluginStateServer ""
       :powerPreference "default"
       :preferWebgl1 false
       :rcsbAssemblySymmetryApplyColors true
       :rcsbAssemblySymmetryDefaultServerType "full"
       :rcsbAssemblySymmetryDefaultServerUrl ""
       :resolutionMode "auto"
       :saccharideCompIdMapType "default"
       :transparency true
       :viewportShowAnimation false
       :viewportShowControls true
       :viewportShowExpand true
       :viewportShowSelectionMode false
       :viewportShowSettings true
       :viewportShowTrajectoryControls false
       :volumeStreamingDisabled false
       ;;  :volumeStreamingServer ""
       })

(defui molstar-viewer [{:keys [mvs-data other-props]}]
  (let [container-ref (uix/use-ref nil)
        [molstar-instance, set-molstar-instance] (uix/use-state nil)]

    (uix/use-effect
     (fn []
       (when @container-ref
         (-> (js/molstar.Viewer.create @container-ref molstar-params)
             (.then (fn [instance] (set-molstar-instance instance))))))
     [])

    ;; Effect for data updates
    (uix/use-effect
     (fn []
       (when (and molstar-instance mvs-data)
         (.loadPdb ^js molstar-instance mvs-data)))
     [molstar-instance mvs-data])

    ($ :div.molstar-container
       ($ :div.molstar {:ref container-ref}))))

(defui molstar-viewer-mvsdata [{:keys [mvs-data other-props]}]
  (let [container-ref (uix/use-ref nil)
        [molstar-instance, set-molstar-instance] (uix/use-state nil)]

    ;; Effect for initialization
    (uix/use-effect
     (fn []
       (when @container-ref
         (-> (js/molstar.Viewer.create @container-ref molstar-params)
             (.then (fn [instance] (set-molstar-instance instance))))))
     [])

    ;; Effect for data updates
    (uix/use-effect
     (fn []
       ;; (print "in the effect")
       ;; (print (js/JSON.stringify mvs-data))
       (when (and molstar-instance mvs-data)
         (.loadMvsData ^js molstar-instance (js/JSON.stringify mvs-data) "mvsj" #js {:replaceExisting true})))
     [molstar-instance mvs-data])

    ($ :div.molstar-container
       ($ :div.molstar {:ref container-ref}))))

(def mvs-sample
  (clj->js   {:kind "single"
              :root {:kind "root"
                     :children [{:kind "download"
                                 :params {:url "https://files.wwpdb.org/download/1cbs.cif"}
                                 :children [{:kind "parse"
                                             :params {:format "mmcif"}
                                             :children [{:kind "structure"
                                                         :params {:type "model"}
                                                         :children [{:kind "component"
                                                                     :params {:selector "all"}
                                                                     :children [{:kind "representation"
                                                                                 :params {:type "cartoon"}
                                                                                 :children [{:kind "color"
                                                                                             :params {:color "blue"}}]}]}]}]}]}]}
              :metadata {:timestamp "2025-04-14T19:04:58.549065+00:00"
                         :version "1.4"}}))
