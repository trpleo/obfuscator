package obfuscator

import cats.data.Validated._
import cats.data.ValidatedNel
import cats.implicits._

sealed trait ValidatorNel {

  type ValidationResult[A] = ValidatedNel[String, A]

  def validateReferences(refs: List[Int], msg: String)(implicit splitted: List[String]): ValidationResult[List[Int]] =
    if (refs.max < splitted.size) refs.validNel else msg.invalidNel

  def validateCrossRefs(refs0: List[Int], refs1: List[Int]): ValidationResult[(List[Int], List[Int])] =
    if (refs0.intersect(refs1).isEmpty) (refs0, refs1).validNel else "err1".invalidNel

  def validate(refs0: List[Int], refs1: List[Int])(implicit splitted: List[String]) =
    (validateReferences(refs0, "err0/0"),
      validateReferences(refs1, "err0/1"),
      validateCrossRefs(refs0, refs1)
    ).mapN{ case t => t._3 }
}

object VN extends ValidatorNel