name := "scala-fun"

scalaVersion in ThisBuild := "2.12.2"

git.gitTagToVersionNumber := { tag: String =>
  if(tag matches "[0-9]+\\..*") Some(tag)
  else None
}
git.useGitDescribe := true

lazy val backend = crossProject.crossType(CrossType.Pure).in(file("backend"))
  .settings(version := "0.0.2")
  .jvmSettings(
    resolvers += Resolver.bintrayRepo("hseeberger", "maven"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.0.6",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.4",
      "de.heikoseeberger" %% "akka-http-circe" % "1.15.0",
      "ch.megard"         %% "akka-http-cors" % "0.2.1",
      "io.circe"          %% "circe-core" % "0.7.0",
      "io.circe"          %% "circe-generic" % "0.7.0",
      "io.circe"          %% "circe-parser" % "0.7.0",
      "io.taig"           %% "communicator" % "3.2.2",
      "io.lemonlabs"      %% "scala-uri" % "0.4.16"
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js"  %%% "scalajs-dom" % "0.9.1",
      "io.circe"      %%% "circe-core" % "0.7.0",
      "io.circe"      %%% "circe-generic" % "0.7.0",
      "io.circe"      %%% "circe-parser" % "0.7.0"
    )
  )
  .enablePlugins(ScalaJSPlugin, GitVersioning)

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
    dockerBaseImage := "openjdk:8-jre-alpine",
    dockerExposedPorts := Seq(sys.props.getOrElse("API_PORT", 8080).asInstanceOf[Int]),
    dockerRepository in Docker := Some("spikerlabs"),
    packageName in Docker := "scala-fun-backend",
    version in Docker := git.gitDescribedVersion.value.getOrElse("latest"),
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.0" % "test"
  )
  .enablePlugins(AshScriptPlugin, DockerPlugin)

onLoad in Global := (Command.process("project api", _: State)) compose (onLoad in Global).value