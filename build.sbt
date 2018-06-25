organization :="ch.acmesoftware"

name := "typeswagger"

scalaVersion := "2.12.6"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-nullary-override",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused",
  "-Ypartial-unification"
)

scalacOptions in (Compile, doc) ++= Seq(
  "-doc-root-content", baseDirectory.value+"/src/main/scaladoc/rootdoc.txt"
)

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-native" % "3.5.4",
  "org.typelevel" %% "cats-core" % "1.0.1",
  "org.scalactic" %% "scalactic" % "3.0.5" % "test",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)