---Add molstar css and js dependencies.
---Add livemol js and css
local function addDependencies()
    quarto.doc.addHtmlDependency {
        name = 'molstar',
        version = 'v4.14.1',
        scripts = { './assets/molstar.js' },
        stylesheets = { './assets/molstar.css', 'assets/app-container.css' },
    }
    quarto.doc.addHtmlDependency {
        name = 'livemol',
        version = 'v0.1.0',
        scripts = { './assets/main.js' },
        stylesheets = { './assets/main.css' },
    }
end


local function init_livemol(divname)
    return string.format([[
    <script>
      document.addEventListener('DOMContentLoaded', function() {
        livemol.init('%s');
      });
    </script>
  ]], divname)
end


-- return {
--     ['livemol'] = function(args, kwargs, meta, raw_args, context)
--         return pandoc.Str("Hello from Shorty!")
--     end
-- }

-- return {
--     ['livemol'] = function(args, kwargs, meta, raw_args, context)
--         -- Create a string representation of the args table
--         local args_str = ""
--         for i, arg in ipairs(args) do
--             args_str = args_str .. "Arg " .. i .. ": " .. tostring(arg) .. "\n"
--         end

--         -- If you want to print kwargs too
--         local kwargs_str = ""
--         for key, value in pairs(kwargs) do
--             kwargs_str = kwargs_str .. "Kwarg " .. key .. ": " .. tostring(value) .. "\n"
--         end

--         return pandoc.Para(pandoc.Str("Arguments: \n" .. args_str .. "\nKeyword arguments: \n" .. kwargs_str))
--     end
-- }

return {
    ['livemol'] = function(args, kwargs, meta, raw_args, context)
        -- Create a string representation of the args table
        local args_str = ""
        for i, arg in ipairs(args) do
            args_str = args_str .. "Arg " .. i .. ": " .. tostring(arg) .. "\n"
        end

        -- If you want to print kwargs too
        local kwargs_str = ""
        for key, value in pairs(kwargs) do
            kwargs_str = kwargs_str .. "Kwarg " .. key .. ": " .. tostring(value) .. "\n"
        end



        return pandoc.Para(pandoc.Str("Arguments: \n" .. args_str ..
            "\nKeyword arguments: \n" .. kwargs_str ..
            "\nContext: \n" .. context))
    end
}
