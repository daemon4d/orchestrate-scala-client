name := "orchestrate-scala-client"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.scaldi" %% "scaldi" % "0.5.6",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4-SNAPSHOT",
  "com.typesafe.play" %% "play-ws" % "2.4.0-M2",
  "org.scaldi" %% "scaldi-akka" % "0.5.6",
  "joda-time" % "joda-time" % "2.8.1",
  "com.google.code.gson" % "gson" % "1.7.1"
)

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"