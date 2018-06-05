package obfuscator

import cats.data.Validated._
import cats.data.ValidatedNel
import cats.implicits._

import scala.util.Try

sealed trait ValidatorNel {

  type ValidationResult[A] = ValidatedNel[String, A]

  def validateNumeric(refs: List[Int], msg: String)(implicit splitted: List[String]): ValidationResult[Boolean] =
    splitted
      .zipWithIndex
      .filter(idx => refs.contains(idx._2))
      .map(s => Try(s._1.toDouble).toOption)
      .filter(_.isEmpty) match {
      case l if (l.size == 0) => true.validNel
      case l => s"$msg - [${l.size}] from [${refs.size}] columns.".invalidNel
    }

  def validateReferences(refs: List[Int], msg: String)(implicit splitted: List[String]): ValidationResult[List[Int]] =
    if (refs.max < splitted.size) refs.validNel else msg.invalidNel

  def validateCrossRefs(refs0: List[Int], refs1: List[Int]): ValidationResult[(List[Int], List[Int])] =
    if (refs0.intersect(refs1).isEmpty) (refs0, refs1).validNel else "err1".invalidNel

  def validateRowAgainstColDefs(obf: List[Int], obfErr: String, scale: List[Int], scaleErr: String, lnCnt: Int)(implicit splitted: List[String]) =
    ( validateReferences(obf, obfErr), validateReferences(scale, scaleErr), validateNumeric(scale, s"Not numeric value at line [$lnCnt]") ).mapN{ case (a,b,_) => (a,b) }

  def validateColDefs(refs0: List[Int], refs1: List[Int]) =
    ( validateCrossRefs(refs0, refs1) ).map{ case t => t }
}

object VN extends ValidatorNel