// livemol.js - Molecule viewer integration for Quarto
document.addEventListener('DOMContentLoaded', function() {
  // Find all livemol containers
  const livemolContainers = document.querySelectorAll('.livemol-container');
  
  livemolContainers.forEach(function(container, index) {
    const id = container.id;
    const dataContent = container.getAttribute('data-content');
    
    try {
      // Parse Clojure-like syntax (vector notation)
      // This is a simple parser for Clojure-like syntax commonly used in Reagent/React
      let config;
      
      if (dataContent.trim().startsWith('[')) {
        // Handle Clojure vector notation
        config = parseClojureNotation(dataContent);
      } else {
        // Fallback to JSON if it's not Clojure notation
        config = JSON.parse(dataContent);
      }
      
      // Initialize the viewer
      initializeMoleculeViewer(id, config);
    } catch (error) {
      // Display error in the container
      container.innerHTML = `
        <div class="livemol-error">
          Error initializing molecule viewer: ${error.message}
        </div>
      `;
      console.error("Livemol error:", error);
    }
  });
});

// Function to initialize the molecule viewer
function initializeMoleculeViewer(containerId, config) {
  const container = document.getElementById(containerId);
  
  // Show loading state
  container.innerHTML = '<div class="livemol-loading">Loading molecule viewer...</div>';
  
  // Placeholder for actual molecule viewer initialization
  // This would typically connect to a library like Mol* or 3Dmol.js
  
  // For demonstration purposes, we'll just show that we processed the data
  setTimeout(() => {
    const viewerDiv = document.createElement('div');
    viewerDiv.className = 'livemol-viewer';
    
    // Add some placeholder content showing the configured data
    viewerDiv.innerHTML = `
      <div class="livemol-controls">
        <button onclick="console.log('Rotate clicked')">Rotate</button>
        <button onclick="console.log('Reset clicked')">Reset</button>
      </div>
      <div style="padding: 20px;">
        <p>Molecule Viewer Placeholder</p>
        <pre>${JSON.stringify(config, null, 2)}</pre>
      </div>
    `;
    
    // Replace loading state with viewer
    container.innerHTML = '';
    container.appendChild(viewerDiv);
  }, 500);
}

// Parse Clojure-like notation (very simplified)
function parseClojureNotation(code) {
  // This is a very simplified parser for basic Clojure/EDN notation
  // It handles common patterns like [:div {} [...]] but is not a full parser
  try {
    // Remove newlines and extra spaces for easier parsing
    const cleaned = code.replace(/\s+/g, ' ').trim();
    
    // For simple keyword-map-vector pattern like [:div {} [...]]
    const match = cleaned.match(/\[:([\w-]+)\s+(\{\s*\}|\{.*?\})\s*(.*)\]/);
    
    if (match) {
      return {
        type: match[1],         // The component type (e.g., 'div')
        props: match[2] !== '{}' ? parseClojureMap(match[2]) : {}, // Props/attributes
        content: match[3].trim() // The content/children
      };
    }
    
    // Fallback for other patterns
    return { raw: cleaned };
  } catch (e) {
    console.error("Error parsing Clojure notation:", e);
    return { error: e.message, raw: code };
  }
}

// Helper to parse Clojure maps like {:key "value" :key2 value2}
function parseClojureMap(mapStr) {
  const result = {};
  // Very simplified - in a real app you'd want a proper parser
  // This handles basic {:key "value" :key2 value2} patterns
  const entries = mapStr.slice(1, -1).split(/\s+:/).filter(Boolean);
  
  entries.forEach(entry => {
    const [key, value] = entry.split(/\s+/, 2);
    if (key && value) {
      // Remove quotes if string
      result[key] = value.replace(/^"(.*)"$/, '$1');
    }
  });
  
  return result;
}

// Utility function to generate unique IDs for multiple viewers on one page
window.livemolGenerateId = function() {
  return 'livemol-' + Math.random().toString(36).substr(2, 9);
};