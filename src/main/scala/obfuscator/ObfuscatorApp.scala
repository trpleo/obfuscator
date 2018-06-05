package obfuscator

import java.io.{BufferedWriter, FileWriter}
import java.security.MessageDigest

import cats.data.Validated._

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

  def obfuscateARow(lnCnt: Int)(implicit splitted: List[String]) =
    VN.validateRowAgainstColDefs(
      toObfuscate,
      "Column defs in validateRowAgainstColDefs is wrong (cannot obfuscate column which does not exists).",
      toScale,
      "Column defs in columnListToScale is wrong (cannot scale column which does not exists).",
      lnCnt
    )
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

  def obfuscateAndWrite(ln: String, lnCnt: Int)(implicit writer: BufferedWriter) = {
    obfuscateARow(lnCnt)(ln.split(separator).toList) match {
      case Valid(l) =>
        writeLine(l.mkString(separator))(writer)
      case Invalid(err) =>
        println(s"Invalid row: [$err]")
    }
  }

  def writeLine(s: String)(implicit writer: BufferedWriter) = {
    writer.write(s)
    writer.write("\r\n")
  }

  VN.validateColDefs(toObfuscate, toScale).map {
    case (obftor, scaler) => // XXX: not used, because of the scope, and since these are vals...
      Try(Source.fromFile(inputFullPath))
        .flatMap { source =>
          Success {
            source.getLinesAndClose().foldLeft(0) {
              case (acc, l) if (acc == 0 && skipHeader) =>
                writeLine(l)
                1
              case (acc, ln) =>
                obfuscateAndWrite(ln, acc)
                acc + 1
            }
          }
        }
  } match {
    case Valid(Success(cnt)) =>
      w.flush()
      w.close()
      println(s"Data was written out. [$cnt] lines were processed.")
    case Valid(Failure(t)) =>
      println(s"Writer failed: [${t.getMessage}]")
    case Invalid(err) =>
      println(s"Invalid column def: [$err]")
  }
}
