#!/usr/bin/env bash
sbt "apiClientJs/fullOptJS"
export SCALA_API_VERSION=$(sbt -no-colors --info ";project apiClientJs; version" | tail -1 | sed 's/\[info\][^0-9a-zA-Z]*//' | sed 's/\-[a-zA-Z].*$//')
cd api-client-js
mkdir src/main/resources/lib
cp -f target/scala-2.12/api-client-js-opt.js src/main/resources/lib/index.js
cd src/main/resources
rm -f scala-fun-client*.tgz
sed "s/%%VERSION%%/$SCALA_API_VERSION/" package.template.json > package.json
npm pack
cd ../../../../
ls api-client-js/src/main/resources/scala-fun-client*.tgz
