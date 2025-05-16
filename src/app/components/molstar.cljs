(ns app.components.molstar
  (:require
   [uix.core :as uix :refer [defui $]]
   [uix.dom]
   [uix.re-frame :as urf]))

(def molstar-params
  #js {:allowMajorPerformanceCaveat true
       :collapseLeftPanel false
       :collapseRightPanel false
       :customFormats []
       :disableAntialiasing false
       :disabledExtensions []
       :emdbProvider "rcsb"
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

(defui MolstarMVS []
  (let [mvsj-data (urf/use-subscribe [:app/mvsj])
        container-ref (uix/use-ref nil)
        [molstar-instance, set-molstar-instance] (uix/use-state nil)]

    (uix/use-effect
     (fn []
       (when @container-ref
         (-> (js/molstar.Viewer.create @container-ref molstar-params)
             (.then (fn [instance] (set-molstar-instance instance))))))
     [])

    ;; Updates
    (uix/use-effect
     (fn []
       (when (and molstar-instance mvsj-data)
         (js/console.log "Loading MVSJ data into MolStar:" (clj->js mvsj-data))
         (.loadMvsData
          ^js molstar-instance
          (js/JSON.stringify (clj->js mvsj-data))
          "mvsj"
          #js {:replaceExisting true})))
     [molstar-instance mvsj-data])

    ($ :div.molstar-container
       ($ :div.molstar {:ref container-ref}))))
