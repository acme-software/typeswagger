package ch.acmesoftware.typeswagger.v3

import cats.data.Validated._
import cats.data.{NonEmptyList => NEL, _}
import cats.implicits._

sealed trait Validator {

  type ValidationResult[A] = ValidatedNel[ValidationError, A]

  def apply(openApi: OpenApi): ValidationResult[OpenApi] = {
    (
      validateOpenApiVersion(openApi.openapi),
      validateInfo(openApi.info),
      validateServers(openApi.servers),
      validateTags(openApi.tags),
      validatePaths(openApi.paths),
    ).mapN(OpenApi.apply)
  }

  private def validateOpenApiVersion(version: String): ValidationResult[String] =
    if (version.split(".").length == 3) version.validNel else InvalidOpenApiVersion.invalidNel


  private def validateInfo(info: Info): ValidationResult[Info] = info.termsOfService.map(tos =>
    if (tos.startsWith("/") || tos.startsWith("http://") || tos.startsWith("https://")) info.validNel else InvalidTosReference.invalidNel
  ).getOrElse(info.validNel)

  private def validateServers(servers: Seq[Server]): ValidationResult[Seq[Server]] = ???

  private def validateTags(tags: Seq[Tag]): ValidationResult[Seq[Tag]] = ???

  private def validatePaths(paths: Seq[Path]): ValidationResult[Seq[Path]] = ???


  sealed trait ValidationError {
    def errorMessage: String
  }

  case object InvalidTosReference extends ValidationError {
    override def errorMessage: String = "info.termsOfService must be a valid URI"
  }

  case object InvalidOpenApiVersion extends ValidationError {
    override def errorMessage: String = "openapi must be a valid semantic version (e.g. 3.0.0)"
  }

}

object Validator extends Validator
