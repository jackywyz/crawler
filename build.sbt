name :="craw"

organization := "com.blue"

version :="0.1.3"

scalaVersion := "2.9.2"

//mainClass in (Compile,run) := Some("Hello")

libraryDependencies ++= Seq(
"nu.validator.htmlparser" % "htmlparser" % "1.4",
"com.twitter" % "util-eval" % "5.3.0"
)


