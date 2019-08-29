val akkaVersion = "2.5.24"
val scala = "2.13.0"

enablePlugins(TestNGPlugin)

// to be able to use exit(int) I fork the jvm and kill the fork and not the sbt-shell
fork in run := true

maintainer := "Benjamin Feldmann <benjamin-feldmann@web.de>"

lazy val hitucc = (project in file("."))
  .settings(
    name := "HitUCC",
    libraryDependencies ++= Seq(
      // -- Logging --
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "ch.qos.logback" % "logback-core" % "1.2.3",
      // -- Akka --
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-remote" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      // -- Tests --
      "org.testng" % "testng" % "6.14.2",
      // -- other stuff --
      "org.apache.commons" % "commons-collections4" % "4.0",
      "com.beust" % "jcommander" % "1.72",
      "com.twitter" %% "chill-akka" % "0.9.3",
      "org.projectlombok" % "lombok" % "1.18.2",
      "org.apache.commons" % "commons-collections4" % "4.0",
      "com.opencsv" % "opencsv" % "3.3",
    ),
  )

// define tasks
TaskKey[Unit]("bridgesTask") := (run in Compile).toTask(" peer-host --workers 4 -ddf 8 -i bridges.csv --csvDelimiter ,").value
TaskKey[Unit]("bridgesTaskManyWorker") := (run in Compile).toTask(" peer-host --workers 36 -ddf 8 -i bridges.csv --csvDelimiter ,").value
TaskKey[Unit]("flightTask") := (run in Compile).toTask(" flight peer host system").value
TaskKey[Unit]("ncvoterTask") := (run in Compile).toTask(" peer-host --workers 4 -ddf 3 -i ncvoter_Statewide.10000r.csv --csvDelimiter , --csvSkipHeader").value
TaskKey[Unit]("ncvoterTaskSingleWorker") := (run in Compile).toTask(" peer-host --workers 1 -ddf 3 -i ncvoter_Statewide.10000r.csv --csvDelimiter , --csvSkipHeader").value
TaskKey[Unit]("ncvoterPeerTask") := (run in Compile).toTask(" peer --workers 4 --masterhost 169.254.94.1").value
TaskKey[Unit]("chessTask") := (run in Compile).toTask(" peer-host --workers 6 -i chess.csv --csvDelimiter , --csvSkipHeader").value
TaskKey[Unit]("chessTaskSingle") := (run in Compile).toTask(" peer-host --workers 1 -i chess.csv --csvDelimiter , --csvSkipHeader").value

enablePlugins(JavaAppPackaging)