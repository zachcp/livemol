# livemol

## Overview

This repo is currently 3 different things:

1. A web app
2. A quarto extension
3. A documentation site for that extension.

- The webapp is a cljs/UIX app.
- the Quarto extension packages up that app as a `filter`
- the `livemol-docs` folder builds a live website using the filter aboce


## Lifecycle

```shell
# uses npm to run the UIX component
just run
# uses npm tbo build the app then Quarto to build up-to-date docs.
just build-docs
```


## CLJS Development

```shell
npm i # install NPM deps
npm run dev # run dev build in watch mode with CLJS REPL
npm run release # build production bundle
```
