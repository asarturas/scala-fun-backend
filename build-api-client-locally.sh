#!/usr/bin/env bash
sbt "apiClientJs/fullOptJS"
export SCALA_API_VERSION=$(sbt -no-colors --info ";project apiClientJs; version" | tail -1 | sed 's/\[info\][^0-9a-zA-Z]*//' | sed 's/\-[a-zA-Z].*$//')
cd api-client-js
mkdir -p src/main/resources/localbuild/lib
cp -f target/scala-2.12/api-client-js-opt.js src/main/resources/localbuild/lib/index.js
cd src/main/resources/localbuild/
cp ../index.d.ts .
cp ../index.js .
sed "s/%%VERSION%%/$SCALA_API_VERSION/" ../package.template.json > package.json
rm -f scala-fun-client*.tgz
npm pack
cd ../../../../../
ls api-client-js/src/main/resources/localbuild/scala-fun-client*.tgz
