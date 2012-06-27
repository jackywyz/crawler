name :="craw"

organization := "com.blue"

version :="1.1"

scalaVersion := "2.9.2"

//mainClass in (Compile,run) := Some("Hello")

libraryDependencies ++= Seq(
"nu.validator.htmlparser" % "htmlparser" % "1.2.1",
"mysql" % "mysql-connector-java" % "5.1.12"
)


