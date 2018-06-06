package obfuscator

import java.io.File
import scala.collection.JavaConverters._
import com.typesafe.config.ConfigFactory

trait AppConfig {
  lazy val defaultConfig= ConfigFactory.parseResources("defaults.conf")
//  lazy val fallbackConfig = ConfigFactory.parseFile(new File("./obfuscation.conf")).withFallback(defaultConfig).resolve()
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
