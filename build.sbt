name := "scala-fun"

scalaVersion in ThisBuild := "2.12.2"

lazy val backend = crossProject.crossType(CrossType.Pure).in(file("backend"))
  .settings(
    version := "0.0.1",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % "0.7.0",
      "io.circe" %%% "circe-generic" % "0.7.0",
      "io.circe" %%% "circe-parser" % "0.7.0"
    )
  )
  .jvmSettings(
    resolvers += Resolver.bintrayRepo("hseeberger", "maven"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.0.6",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.4",
      "de.heikoseeberger" %% "akka-http-circe" % "1.15.0",
      "ch.megard"         %% "akka-http-cors" % "0.2.1"
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1"
    )
  )

lazy val backendJvm = backend.jvm
lazy val backendJs = backend.js

lazy val apiClientJs: Project = (project in file("api-client-js"))
  .dependsOn(backendJs)
  .settings(name := "api-client-js")
  .enablePlugins(ScalaJSPlugin)

lazy val api: Project = (project in file("api"))
  .dependsOn(backendJvm)
  .settings(name := "api")
