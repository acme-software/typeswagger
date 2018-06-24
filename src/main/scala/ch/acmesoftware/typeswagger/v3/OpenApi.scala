package ch.acmesoftware.typeswagger.v3

import ch.acmesoftware.typeswagger.v3.OpenApi._

import scala.language.implicitConversions

case class OpenApi(openapi: String = "3.0.0", info: Info, servers: Seq[Server] = Nil, tags: Seq[Tag] = Nil, paths: Seq[Path] = Nil) {

  def withInfo(license: Option[String] = None, licenseUrl: Option[String] = None,
               contactName: Option[String] = None, contactUrl: Option[String] = None, contactEmail: Option[String] = None) =
    copy(info = info.copy(license = Some(License(license, licenseUrl)), contact = Some(Contact(contactName, contactUrl, contactEmail))))

  def withServer(url: String, description: Option[String] = None): OpenApi =
    copy(servers = servers :+ Server(url, description))

  def withTag(name: String, description: Option[String] = None, externalDocs: Option[ExternalDoc] = None): OpenApi =
    copy(tags = tags :+ Tag(name, description, externalDocs))

  @deprecated
  def withPath(path: String, summary: Option[String] = None, description: Option[String] = None, servers: Seq[Server] = Nil, parameters: Seq[Parameter] = Nil)
              (op1: HttpOperation, opn: HttpOperation*): OpenApi = {
    copy(paths = paths :+ Path(path, summary, description, servers, parameters, (Seq(op1) ++ opn).toMap))
  }

  def path(path: String, summary: Option[String] = None, description: Option[String] = None, servers: Seq[Server] = Nil, parameters: Seq[Parameter] = Nil)
          (ops: Seq[HttpOperation]): OpenApi =
    copy(paths = paths :+ Path(path, summary, description, servers, parameters, ops.toMap))

  def toJson(): String = Json(this)
}

object OpenApi {

  type HttpOperation = (Method, Operation)

  def create(title: String, version: String, description: Option[String] = None, termsOfService: Option[String] = None): OpenApi =
    OpenApi(info = Info(title = title, version = version, description = description, termsOfService = termsOfService))


  def doc(url: String, description: Option[String] = None): ExternalDoc = ExternalDoc(url, description)

  def op(summary: Option[String] = None, description: Option[String] = None, operationId: Option[String] = None,
         tags: Seq[String] = Nil, externalDocs: Option[ExternalDoc] = None) =
    Operation(summary, description, operationId, tags, externalDocs, parameters = Nil, requestBody = None,
      responses = Map(DEFAULT -> Response("Default Response")), callbacks = Nil, servers = Nil, security = Nil, deprecated = false)


  //TODO: complete
  sealed trait Method {
    def str(): String

    def >>(operation: Operation): HttpOperation = this -> operation
  }

  case object GET extends Method {
    override def str(): String = "get"
  }

  case object POST extends Method {
    override def str(): String = "post"
  }

  case object DELETE extends Method {
    override def str(): String = "delete"
  }

  //TODO: complete
  sealed trait StatusCode {
    def str(): String
  }

  case object DEFAULT extends StatusCode {
    override def str(): String = "default"
  }

  case object OK extends StatusCode {
    override def str(): String = "200"
  }


  sealed trait ParameterLocation {
    def str(): String
  }

  case object QUERY extends ParameterLocation {
    override def str(): String = "query"
  }

  case object HEADER extends ParameterLocation {
    override def str(): String = "header"
  }

  case object PATH extends ParameterLocation {
    override def str(): String = "path"
  }

  case object COOKIE extends ParameterLocation {
    override def str(): String = "cookie"
  }

  object Schema {
    val string = Schema("string")

    val int = Schema("integer")
  }
}

case class Info(title: String, description: Option[String] = None, termsOfService: Option[String] = None, contact: Option[Contact] = None, license: Option[License] = None, version: String)

case class Contact(name: Option[String], url: Option[String], email: Option[String])

case class License(name: Option[String], url: Option[String])

case class Server(url: String, description: Option[String] = None) //TODO: add variables: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#serverVariableObject

case class Tag(name: String, description: Option[String], externalDocs: Option[ExternalDoc] = None)

case class ExternalDoc(url: String, description: Option[String] = None)

case class Path(path: String, summary: Option[String], description: Option[String], servers: Seq[Server], parameters: Seq[Parameter],
                operations: Map[Method, Operation])

case class Operation(summary: Option[String], description: Option[String], operationId: Option[String], tags: Seq[String],
                     externalDocs: Option[ExternalDoc], parameters: Seq[Parameter], requestBody: Option[RequestBody],
                     responses: Map[StatusCode, Response], callbacks: Seq[Callback], deprecated: Boolean,
                     security: Seq[SecurityRequirement], servers: Seq[Server]) {

  def withParameter(name: String, in: ParameterLocation, schema: Schema, description: Option[String] = None, required: Boolean = false,
                    deprecated: Boolean = false, allowEmptyValue: Boolean = true): Operation =
    copy(parameters = parameters :+ Parameter(name, in, schema, description, required, deprecated, allowEmptyValue))

  def withTag(name: String): Operation =
    copy(tags = tags :+ name)

  //TODO: add other builders
}

case class Parameter(name: String, in: ParameterLocation, schema: Schema, description: Option[String], required: Boolean,
                     deprecated: Boolean, allowEmptyValue: Boolean)

case class RequestBody()

case class Response(description: String)

case class Schema(schemaType: String) //TODO: complete

case class Callback()

case class SecurityRequirement()