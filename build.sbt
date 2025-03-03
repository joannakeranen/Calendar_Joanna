ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file("."))
  .settings(
    name := "Joannan kalenteri"
  )

libraryDependencies += "org.scalafx" % "scalafx_3" % "20.0.0-R31"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.32.0"

libraryDependencies += "org.mnode.ical4j" % "ical4j" % "4.0.0-rc5"