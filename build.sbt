
name := "akka-data-pipeline"

version := "0.1.0"

organization := "tsheppard01"

scalaVersion := "2.11.12"

//==================================================================================================
// DEPENDENCIES
//==================================================================================================

// Library versions
lazy val akkaVersion = "2.5.6"
lazy val avroVersion = "1.8.2"
lazy val mockitoVersion = "1.9.5"
lazy val scalaTestVersion = "3.0.1"
lazy val slf4jVersion = "1.7.25"

// Dependency declarations
lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
lazy val avro = "org.apache.avro" % "avro" % avroVersion
lazy val slf4jApi = "org.slf4j" % "slf4j-api" % slf4jVersion
lazy val slf4jSimple = "org.slf4j" % "slf4j-simple" % slf4jVersion

lazy val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
lazy val mockitoAll = "org.mockito" % "mockito-all" % mockitoVersion
lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion

// App dependencies
lazy val akkaDataPipelineDependencies = Seq(
  akkaActor,
  avro,
  akkaSlf4j,
  slf4jApi,
  slf4jSimple
)

// Test dependencies
lazy val testDependencies = Seq(
  mockitoAll,
  scalaTest,
  akkaTestKit
)

libraryDependencies ++= akkaDataPipelineDependencies ++ testDependencies