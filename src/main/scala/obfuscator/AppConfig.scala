package obfuscator

import java.io.File
import scala.collection.JavaConverters._
import com.typesafe.config.ConfigFactory

trait AppConfig {
  val defaultConfig= ConfigFactory.parseResources("defaults.conf")
  val fallbackConfig = ConfigFactory.parseFile(new File("./obfuscation.conf")).withFallback(defaultConfig).resolve()

  val inputFullPath = fallbackConfig.getString("obfuscator.inputFile")
  val outputFullPath = fallbackConfig.getString("obfuscator.outputFile")

  val separator = fallbackConfig.getString("obfuscator.separator")
  val toObfuscate = fallbackConfig.getIntList("obfuscator.columnListToObfuscate").asScala.toList.map(_.toInt)
  val toScale = fallbackConfig.getIntList("obfuscator.columnListToScale").asScala.toList.map(_.toInt)
  val secret = fallbackConfig.getDouble("obfuscator.scaleFactor")
}
