name := "scala-fun"

version := "1.0"

scalaVersion := "2.12.2"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.6"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.4"
libraryDependencies += "de.heikoseeberger" %% "akka-http-circe" % "1.15.0"

libraryDependencies += "io.circe" %% "circe-parser" % "0.7.1"
libraryDependencies += "io.circe" %% "circe-generic" % "0.7.1"



//val circeVersion = "0.8.0"

//libraryDependencies ++= Seq(
//  "io.circe" %% "circe-core",
//  "io.circe" %% "circe-generic",
//  "io.circe" %% "circe-parser"
//).map(_ % circeVersion)