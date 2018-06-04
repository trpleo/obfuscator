package obfuscator

import java.io.{BufferedWriter, FileWriter}
import java.security.MessageDigest

import cats.Traverse
import cats.data.Validated._
import cats.implicits._

import scala.io.Source
import scala.util.{Failure, Success, Try}

object ObfuscatorApp extends App with AppConfig {

  val w = new BufferedWriter(new FileWriter(outputFullPath))
  def obfuscateString(s: String): String = MessageDigest.getInstance("MD5").digest(s.getBytes).mkString
  def obfuscatorForDouble(secret: Double, d: Double): Double = d * secret
  def obfuscateNumeric(s: String): Double =
    Try(s.toDouble) match {
      case Success(d) => obfuscatorForDouble(secret, d)
      case Failure(_) => Double.MinValue
    }

  def obfuscateARow(implicit splitted: List[String]) =
    VN.validate(toObfuscate, toScale)
      .map(_ => splitted.zipWithIndex)
      .map {
        _.map {
          case (v, idx) if (toObfuscate.contains(idx)) => obfuscateString(v)
          case (v, idx) if (toScale.contains(idx))     => obfuscateNumeric(v).toString
          case (v, _)                                  => v
        }
      }

  implicit def toClosingSource(source: Source) = new {
    val lines = source.getLines

    def getLinesAndClose() = new Iterator[String] {
      def hasNext = if (!lines.hasNext) { source.close; false } else true
      def next = lines.next
    }
  }

  Try(Source.fromFile(inputFullPath))
    .flatMap { source =>
      val tries =
        source.getLinesAndClose()
          .map { ln =>
            obfuscateARow(ln.split(separator).toList) match {
              case Valid(l) =>
                Try { w.write(l.mkString(separator)); w.write("\r\n") }
              case Invalid(err) if (err.size == 1) =>
                Success { println(s"Error occured during execution. Error was: [$err]") }
              case Invalid(err) =>
                Success { println(s"Error occured during execution. Errors were: [${err.mkString_("", ",", "")}]") }
            }
          }
      Traverse[List].sequence(tries.toList)
    } match {
      case Success(_) =>
        w.flush()
        w.close()
        println(s"Data was written out.")
      case Failure(t) =>
        println(s"Writer failed: [${t.getMessage}]")
    }
}
