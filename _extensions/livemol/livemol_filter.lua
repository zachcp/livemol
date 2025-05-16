-- Add dependencies for additional libraries and assets
local function ensureHtmlDeps()
    quarto.doc.addHtmlDependency {
        name = 'molstar',
        version = 'v4.14.1',
        scripts = { './assets/molstar.js' },
        stylesheets = { './assets/molstar.css', 'assets/molstar.css' },
    }
    quarto.doc.addHtmlDependency {
        name = 'livemol',
        version = 'v0.1.0',
        scripts = { './assets/main.js' },
        stylesheets = { './assets/main.css' },
    }
end

-- Add initialization script to ensure app is initialized only once
local function ensureAppInitialization()
    -- Add global initialization script that runs once
    quarto.doc.includeText("in-header", [[
<script>
window.livemolInitialized = false;
document.addEventListener("DOMContentLoaded", function() {
    if (!window.livemolInitialized) {
        app.core.init();
        window.livemolInitialized = true;
    }
});
</script>
]])
end

-- Generate a unique ID for the molecule viewer container
local counter = 0
local function generateUniqueId()
    counter = counter + 1
    return "livemol-" .. counter
end

-- Process code blocks with the livemol class
function CodeBlock(el)
    if el.classes and el.classes:includes("livemol") then
        -- Ensure we have the CSS and JS dependencies
        ensureHtmlDeps()

        -- Generate a unique ID for this molecule viewer
        local id = generateUniqueId()

        -- Get the content and escape it for embedding in HTML
        local content = el.text

        -- Process Clojure code if needed (basic validation)
        local structureValid = content:match("%[%s*:") ~= nil

        -- Escape content for HTML attribute
        local escaped_content = content:gsub('&', '&amp;'):gsub('<', '&lt;'):gsub('>', '&gt;'):gsub('"', '&quot;')

        -- Add some classes based on content type
        local classes = "livemol-container"
        if structureValid then
            classes = classes .. " livemol-clojure"
        end
        
        local html = string.format(
            '<div id="%s" class="%s" data-content="%s"></div>\n<script>document.addEventListener("DOMContentLoaded", function() { var el = document.getElementById("%s"); app.core.init("%s", el.getAttribute("data-content")); });</script>',
            id,
            classes,
            escaped_content,
            id,
            id
        )

        -- Return a RawBlock with HTML content
        return pandoc.RawBlock("html", html)
    end
    return el
end

-- Ensure dependencies are added at the beginning of the document
function Meta(meta)
    ensureHtmlDeps()
    ensureAppInitialization()
    return meta
end