name :="craw"

organization := "com.blue"

version :="0.2.0"

scalaVersion := "2.10.0"

//mainClass in (Compile,run) := Some("Hello")

libraryDependencies ++= Seq(
"nu.validator.htmlparser" % "htmlparser" % "1.4",
"com.twitter" % "util-eval" % "5.3.6",
"com.typesafe.akka" %% "akka-actor" % "2.1.0",
"com.typesafe.akka" %% "akka-slf4j" % "2.1.0",
"ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime"
)


