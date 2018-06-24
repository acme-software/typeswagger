package ch.acmesoftware.typeswagger.v3

import ch.acmesoftware.typeswagger.v3.OpenApi._

import scala.language.implicitConversions

object Implicits {

  implicit def nonEmptyStringToOption(in: String): Option[String] = if(in != null && !in.isEmpty) Some(in) else None

  implicit def objectToOption[A](in: A): Option[A] = Option(in)

  implicit def httpOperationToSeq(o: HttpOperation): Seq[HttpOperation] = Seq(o)

  implicit class OperationTupleOps(a: HttpOperation) {
    def ~(b: HttpOperation): Seq[HttpOperation] = Seq(a, b)
  }

  implicit class OperationSeqOps(a: Seq[HttpOperation]) {
    def ~(b: HttpOperation): Seq[HttpOperation] = a :+ b
  }
}
