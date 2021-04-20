

name := """play-slick-example"""

version := "1.0"

scalaVersion := "2.13.5"

lazy val root = (project in file(".")).enablePlugins(PlayScala)


libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "com.h2database" % "h2" % "1.4.187",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "org.mockito" %% "mockito-scala" % "1.11.2",
  specs2 % Test,
  "com.github.jwt-scala" %% "jwt-core" % "7.1.3",
  "mysql" % "mysql-connector-java" % "6.0.6"

)

routesGenerator := InjectedRoutesGenerator

javaOptions in Test += "-Dconfig.file=conf/test.conf"
coverageEnabled := true
