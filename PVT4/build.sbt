import	NaNvePackagerKeys._	

herokuAppName	in	Compile	:=	"blueberry-surprise-6049”

libraryDependencies	+=	"mysql"	%	"mysql-connector-java"	%	"5.1.27"	

name := """PVT4"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)
