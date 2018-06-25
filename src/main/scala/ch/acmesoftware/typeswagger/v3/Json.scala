package ch.acmesoftware.typeswagger.v3

import ch.acmesoftware.typeswagger.v3.OpenApi._

import scala.language.implicitConversions

object Json {

  import org.json4s.JsonAST
  import org.json4s.JsonDSL._
  import org.json4s.native.JsonMethods._

  def apply(in: OpenApi): String = pretty(render(in))

  implicit def convert(in: OpenApi): JsonAST.JObject = ("openapi" -> in.openapi) ~
    ("info" -> in.info) ~
    ("servers" -> in.servers) ~
    ("tags" -> nonEmpty(in.tags)) ~
    ("paths" -> in.paths.map(e => e.path -> e).toMap)

  implicit def convert(info: Info): JsonAST.JObject = ("title" -> info.title) ~
    ("version" -> info.version) ~
    ("description" -> info.description) ~
    ("termsOfService" -> info.termsOfService) ~
    ("license" -> info.license) ~
    ("contact" -> info.contact)

  implicit def convert(license: License): JsonAST.JObject = ("name" -> license.name) ~
    ("url" -> license.url)

  implicit def convert(contact: Contact): JsonAST.JObject = ("name" -> contact.name) ~
    ("url" -> contact.url) ~
    ("email" -> contact.email)

  implicit def convert(server: Server): JsonAST.JObject = ("url" -> server.url) ~
    ("description" -> server.description)

  implicit def convert(path: Path): JsonAST.JObject = ("summary" -> path.summary) ~
    ("description" -> path.description) ~
    ("servers" -> nonEmpty(path.servers)) ~
    ("parameters" -> nonEmpty(path.parameters)) ~
    ("get" -> path.operations.get(GET)) ~
    ("post" -> path.operations.get(POST)) ~
    ("delete" -> path.operations.get(DELETE))

  implicit def convert(operation: Operation): JsonAST.JObject = ("tags" -> nonEmpty(operation.tags)) ~
    ("description" -> operation.description) ~
    ("summary" -> operation.summary) ~
    ("externalDocs" -> operation.externalDocs) ~
    ("operationId" -> operation.operationId) ~
    ("parameters" -> nonEmpty(operation.parameters)) ~
    ("requestBody" -> operation.requestBody) ~
    ("responses" -> nonEmpty(operation.responses.map(r => r._1.str() -> r._2))) ~
    //("callbacks" -> nonEmpty(operation.callbacks)) ~
    ("deprecated" -> operation.deprecated) ~
    //("security" -> nonEmpty(operation.security)) ~
    ("servers" -> nonEmpty(operation.servers))

  implicit def convert(tag: Tag): JsonAST.JObject = ("name" -> tag.name) ~
    ("description" -> tag.description) ~
    ("externalDocs" -> tag.externalDocs)

  implicit def convert(parameter: Parameter): JsonAST.JObject = ("name" -> parameter.name) ~
    ("in" -> parameter.in.str()) ~
    ("schema" -> parameter.schema) ~
    ("description" -> parameter.description) ~
    ("required" -> parameter.required) ~
    ("deprecated" -> parameter.deprecated) ~
    ("allowEmptyValue" -> parameter.allowEmptyValue)

  implicit def convert(schema: Schema): JsonAST.JObject = ("type" -> schema.schemaType) ~
    ("format" -> schema.format)//TODO: complete

  implicit def convert(externalDoc: ExternalDoc): JsonAST.JObject = ("url" -> externalDoc.url) ~
    ("description" -> externalDoc.description)

  implicit def convert(requestBody: RequestBody): JsonAST.JObject = ("content" -> requestBody.content.map(c => c.mimeType -> c).toMap) ~
    ("description" -> requestBody.description) ~
    ("required" -> requestBody.required)

  implicit def convert(mediaType: MediaType): JsonAST.JObject = ("schema" -> mediaType.schema) ~
    ("example" -> mediaType.example)

  implicit def convert(response: Response): JsonAST.JObject = ("description" -> response.description)

  //TODO implicit def convert(callback: Callback): JsonAST.JObject = ("toto" -> true)

  //TODO implicit def convert(securityRequirement: SecurityRequirement): JsonAST.JObject = ("toto" -> true)

  def nonEmpty[A](in: Seq[A]): Option[Seq[A]] = if (in.isEmpty) None else Some(in)

  def nonEmpty[A, B](in: Map[A, B]): Option[Map[A, B]] = if (in.isEmpty) None else Some(in)
}
