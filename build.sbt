import Dependencies._

ThisBuild / scalaVersion := "2.12.13"
ThisBuild / version := "0.0.0"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "google-cloud-storage-sample-scala",
    libraryDependencies += scalaTest % Test
  )
  .settings(
    libraryDependencies ++= {
      Seq(
        "com.github.pathikrit" %% "better-files" % "3.9.1",
        "com.google.cloud" % "google-cloud-storage" % "1.113.16"
      )
    }
  )

