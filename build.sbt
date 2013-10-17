name := "AntGraphVisualizer"

version := "1.0"

scalaVersion := "2.10.2"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies += "jgraph" % "jgraph" % "5.13.0.0"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.RC2" % "test"