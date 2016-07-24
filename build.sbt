name := """fasttwentyone-server"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

val playVersion = "2.5.3"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
    cache,
    ws,
    filters,
    jdbc,
    "org.flywaydb"           %% "flyway-play"                   % "3.0.0",
    "org.scalikejdbc"        %% "scalikejdbc"                   % "2.4.2",
    "org.scalikejdbc"        %  "scalikejdbc-config_2.11"       % "2.4.2",
    "mysql"                  %  "mysql-connector-java"          % "5.1.34",
    "com.etaty.rediscala"    %% "rediscala"                     % "1.4.0",
    //
    "org.mindrot"           %   "jbcrypt"                       % "0.3m",
    //
    "org.slf4j"              %  "slf4j-simple"                  % "1.7.21",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0"  % "test",
    specs2 % Test
)

resolvers ++= Seq(
	"Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
	"scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
	"rediscala" at "http://dl.bintray.com/etaty/maven"
)

doc in Compile <<= target.map(_ / "none")

fork in run := true

// rjs
// RjsKeys.paths += ("jsRoutes" -> ("/jsroutes" -> "empty:"))