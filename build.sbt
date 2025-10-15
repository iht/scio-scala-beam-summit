import sbt._
import Keys._
import com.here.bom.Bom

val scioVersion = "0.14.12"
val beamVersion = "2.63.0"

val guavaVersion = "33.4.0-jre"
val jacksonVersion = "2.18.2"
val magnolifyVersion = "0.8.0"
val nettyVersion = "4.2.7.Final"
val slf4jVersion = "2.0.16"
val gcpLibrariesVersion = "26.55.0"

lazy val gcpBom = Bom(
  "com.google.cloud" % "libraries-bom" % gcpLibrariesVersion
)
lazy val beamBom = Bom("org.apache.beam" % "beam-sdks-java-bom" % beamVersion)
lazy val guavaBom = Bom("com.google.guava" % "guava-bom" % guavaVersion)
lazy val jacksonBom = Bom(
  "com.fasterxml.jackson" % "jackson-bom" % jacksonVersion
)
lazy val magnolifyBom = Bom("com.spotify" % "magnolify-bom" % magnolifyVersion)
lazy val nettyBom = Bom("io.netty" % "netty-bom" % nettyVersion)
lazy val scioBom = Bom("com.spotify" % "scio-bom" % scioVersion)

val bomSettings = Def.settings(
  gcpBom,
  beamBom,
  guavaBom,
  jacksonBom,
  magnolifyBom,
  nettyBom,
  dependencyOverrides ++=
    gcpBom.key.value.bomDependencies ++
      beamBom.key.value.bomDependencies ++
      guavaBom.key.value.bomDependencies ++
      jacksonBom.key.value.bomDependencies ++
      magnolifyBom.key.value.bomDependencies ++
      nettyBom.key.value.bomDependencies
)

lazy val commonSettings = bomSettings ++ Def.settings(
  organization := "example",
  // Semantic versioning http://semver.org/
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.13.16",
  scalacOptions ++= Seq(
    "-release",
    "11",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Ymacro-annotations"
  ),
  javacOptions ++= Seq("--release", "11"),
  // add extra resolved and remove exclude if you need kafka
  // resolvers += "confluent" at "https://packages.confluent.io/maven/",
  excludeDependencies += "org.apache.beam" % "beam-sdks-java-io-kafka",
  excludeDependencies += "com.github.luben" % "zstd-jni"
)

lazy val root: Project = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "scio-scala-workshop-beam-summit",
    description := "scio-scala-workshop-beam-summit",
    publish / skip := true,
    fork := true,
    run / outputStrategy := Some(OutputStrategy.StdoutOutput),
    libraryDependencies ++= Seq(
      "com.spotify" %% "scio-core" % scioVersion,
      "com.spotify" %% "scio-google-cloud-platform" % scioVersion,
      "com.spotify" %% "scio-extra" % scioVersion,
      "com.spotify" %% "scio-test" % scioVersion % Test,
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      "org.apache.beam" % "beam-runners-google-cloud-dataflow-java" % beamVersion % Runtime,
      "org.apache.beam" % "beam-runners-direct-java" % beamVersion % Test,
      "com.spotify" %% "scio-test" % scioVersion % Test,
      "org.slf4j" % "slf4j-simple" % slf4jVersion % Test,
      "com.github.luben" % "zstd-jni" % "1.5.7-1"
    )
  )

lazy val repl: Project = project
  .in(file(".repl"))
  .settings(commonSettings)
  .settings(
    name := "repl",
    description := "Scio REPL for scio-scala-workshop",
    libraryDependencies ++= Seq(
      "com.spotify" %% "scio-repl" % scioVersion
    ),
    Compile / mainClass := Some("com.spotify.scio.repl.ScioShell"),
    publish / skip := true,
    fork := false
  )
  .dependsOn(root)

ThisBuild / versionPolicyIntention := Compatibility.BinaryAndSourceCompatible
