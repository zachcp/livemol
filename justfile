
run:
    npm i && npm run dev


# rarely used
download-deps:
    wget -P _extensions/livemol/assets https://unpkg.com/molstar@4.14.1/build/viewer/molstar.js
    wget -P _extensions/livemol/assets https://unpkg.com/molstar@4.14.1/build/viewer/molstar.css


build-js:
    npm i
    npm run release
    cp public/js/main.js _extensions/livemol/assets
    cp public/main.css _extensions/livemol/assets


preview-docs: build-js
    cp -r _extensions livemol-docs
    quarto preview livemol-docs

build-docs: build-js
    cp -r _extensions livemol-docs
    quarto render livemol-docs
