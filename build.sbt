name := """prototype"""

version := "0.2"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.1.2",
  "com.typesafe.akka" %% "akka-remote" % "2.1.2",
  "com.typesafe.akka" %% "akka-kernel" % "2.1.2" 
)
