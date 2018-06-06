import Dependencies._
import sbtassembly.AssemblyPlugin.defaultShellScript

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.obfuscator",
      scalaVersion := "2.12.6",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "obfuscator",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      cats,
      appconfig
    )
  ).
  settings(
    mainClass in assembly := Some("obfuscator.ObfuscatorApp"),
    assemblyJarName in assembly := s"${name.value}-${version.value}.jar",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript))
  )