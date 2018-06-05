package obfuscator

import java.io.{BufferedWriter, FileWriter}
import java.security.MessageDigest

import cats.Traverse
import cats.data.Validated._
import cats.implicits._

import scala.io.Source
import scala.util.{Failure, Success, Try}

object ObfuscatorApp extends App with AppConfig {

  implicit val w = new BufferedWriter(new FileWriter(outputFullPath))

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

  def obfuscateAndWrite(ln: String)(implicit writer: BufferedWriter) = {
    obfuscateARow(ln.split(separator).toList) match {
      case Valid(l) =>
        writer.write(l.mkString(separator)); writer.write("\r\n"); w.flush()
      case Invalid(err) if (err.size == 1) =>
        println(s"Error occured during execution. Error was: [$err]")
      case Invalid(err) =>
        println(s"Error occured during execution. Errors were: [${err.mkString_("", ",", "")}]")
    }
  }

  Try(Source.fromFile(inputFullPath))
    .flatMap { source =>
      Success{
        source.getLinesAndClose().foldLeft(0l) {
          case (acc, _) if (acc == 0 && skipHeader) => 1l
          case (acc, ln) =>
            obfuscateAndWrite(ln)
            acc + 1l
        }
      }
    } match {
      case Success(_) =>
        w.flush()
        w.close()
        println(s"Data was written out.")
      case Failure(t) =>
        println(s"Writer failed: [${t.getMessage}]")
    }
}
