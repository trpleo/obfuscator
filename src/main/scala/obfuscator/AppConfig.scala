package obfuscator

import java.io.File
import scala.collection.JavaConverters._
import com.typesafe.config.ConfigFactory

trait AppConfig {
  lazy val defaultConfig= ConfigFactory.parseResources("defaults.conf")
  lazy val fallbackConfig = ConfigFactory.parseFile(new File("./obfuscation.conf")).withFallback(defaultConfig).resolve()

  lazy val inputFullPath = fallbackConfig.getString("obfuscator.inputFile")
  lazy val outputFullPath = fallbackConfig.getString("obfuscator.outputFile")

  lazy val separator = fallbackConfig.getString("obfuscator.separator")
  lazy val toObfuscate = fallbackConfig.getIntList("obfuscator.columnListToObfuscate").asScala.toList.map(_.toInt)
  lazy val toScale = fallbackConfig.getIntList("obfuscator.columnListToScale").asScala.toList.map(_.toInt)
  lazy val secret = fallbackConfig.getDouble("obfuscator.scaleFactor")
  lazy val skipHeader = fallbackConfig.getBoolean("obfuscator.skipHeader")
}
