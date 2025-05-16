

download-deps:
    wget -P _extensions/livemol/assets https://unpkg.com/molstar@4.14.1/build/viewer/molstar.js
    wget -P _extensions/livemol/assets https://unpkg.com/molstar@4.14.1/build/viewer/molstar.css


build-js:
    npm run release
    cp public/js/main.js _extensions/livemol/assets
    cp public/main.css _extensions/livemol/assets
