name := """modelado_2_fotos"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  //anorm,
  cache,
  filters,
  ws,
  "com.github.t3hnar" %% "scala-bcrypt" % "2.4",
  "org.xerial" % "sqlite-jdbc" % "3.8.7",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.typesafe.play" %% "play-slick" % "0.8.0"
)
