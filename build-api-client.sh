#!/usr/bin/env bash
sbt "apiClientJs/fullOptJS"
cd api-client-js
cp -f target/scala-2.12/api-client-js-opt.js src/main/resources/lib/index.js
cd src/main/resources
rm -f scala-fun-*.tgz
npm pack
