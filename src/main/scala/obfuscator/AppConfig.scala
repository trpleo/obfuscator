package obfuscator

import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

trait AppConfig {
  lazy val defaultConfig= ConfigFactory.parseResources("defaults.conf")
  val config = ConfigFactory.load().withFallback(defaultConfig).resolve()
  println(config)

  lazy val inputFullPath = config.getString("obfuscator.inputFile")
  lazy val outputFullPath = config.getString("obfuscator.outputFile")

  lazy val separator = config.getString("obfuscator.separator")
  lazy val toObfuscate = config.getIntList("obfuscator.columnListToObfuscate").asScala.toList.map(_.toInt)
  lazy val toScale = config.getIntList("obfuscator.columnListToScale").asScala.toList.map(_.toInt)
  lazy val secret = config.getDouble("obfuscator.scaleFactor")
  lazy val skipHeader = config.getBoolean("obfuscator.skipHeader")
}
