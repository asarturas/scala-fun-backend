name := "scala-fun"

scalaVersion in ThisBuild := "2.12.2"

lazy val backend = crossProject.crossType(CrossType.Pure).in(file("backend"))
  .settings(
    version := "0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-parser" % "0.7.1",
      "io.circe" %% "circe-generic" % "0.7.1"
    )
  )
  .jvmSettings(
    resolvers += Resolver.bintrayRepo("hseeberger", "maven"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.0.6",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.4",
      "de.heikoseeberger" %% "akka-http-circe" % "1.15.0"
    )
  )
  //.jsSettings()

lazy val backendJvm = backend.jvm
lazy val backendJs = backend.js

lazy val apiClientJs: Project = (project in file("api-client-js"))
  .dependsOn(backendJs)
  .settings(name := "api-client-js")
  .enablePlugins(ScalaJSPlugin)

lazy val api: Project = (project in file("api"))
  .dependsOn(backendJvm)
  .settings(
    name := "api",
    (resources in Compile) += (fastOptJS in (apiClientJs, Compile)).value.data,
    (resources in Compile) += (fullOptJS in (apiClientJs, Compile)).value.data
  )

//resolvers += Resolver.bintrayRepo("hseeberger", "maven")

//libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.6"
//libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.4"
//libraryDependencies += "de.heikoseeberger" %% "akka-http-circe" % "1.15.0"

//libraryDependencies += "io.circe" %% "circe-parser" % "0.7.1"
//libraryDependencies += "io.circe" %% "circe-generic" % "0.7.1"



//val circeVersion = "0.8.0"

//libraryDependencies ++= Seq(
//  "io.circe" %% "circe-core",
//  "io.circe" %% "circe-generic",
//  "io.circe" %% "circe-parser"
//).map(_ % circeVersion)
