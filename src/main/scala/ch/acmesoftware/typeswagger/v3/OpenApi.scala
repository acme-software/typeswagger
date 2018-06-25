package ch.acmesoftware.typeswagger.v3

import ch.acmesoftware.typeswagger.v3.OpenApi._

/** Main builder object for OpenAPI v3 specifications
  *
  * @see Companion object functions like [[OpenApi.create]]
  */
case class OpenApi(openapi: String = "3.0.0", info: Info, servers: Seq[Server] = Nil, tags: Seq[Tag] = Nil, paths: Seq[Path] = Nil) {

  /** Adds global information to the API specification
    *
    * All fields an therefore also the whole statement is optional
    *
    * Example:
    * {{{
    * OpenApi.create("Test Spec", "1.0.0").
    *   withInfo(license = "MIT", licenseUrl = "https://opensource.org/licenses/MIT").
    *   withInfo(contactName = "John Doe", contactEmail = "info@acmesoftware.ch",
    *            contactUrl = "https://www.acmesoftware.ch")
    * }}}
    *
    * @param license The name of the license (e.g. "MIT")
    * @param licenseUrl URL, where the license can be reviewed. This can be your own URL or a public one
    * @param contactName Name of the technical contact person (human readable)
    * @param contactUrl Webpage / team page of the maintainance team
    * @param contactEmail E-Mail address for the technical contact
    */
  def withInfo(license: Option[String] = None, licenseUrl: Option[String] = None,
               contactName: Option[String] = None, contactUrl: Option[String] = None, contactEmail: Option[String] = None): OpenApi =
    copy(info = info.copy(license = Some(License(license, licenseUrl)), contact = Some(Contact(contactName, contactUrl, contactEmail))))

  /** Adds a server / endpoint to the API specification
    *
    * The can be called multiple times, to e.g. add staging, integration and
    * production platforms. If using swagger-ui, those servers can be choosen
    * to try-out the endpoints.
    *
    * Example:
    * {{{
    * OpenApi.create("Test Spec", "1.0.0").
    *   withServer("http://localhost").
    *   withServer("http://prod.tld", description = "Production Server")
    * }}}
    *
    * @param url Fully qualified url of the endpoint (e.g.)
    * @param description Optional description of the server
    */
  def withServer(url: String, description: Option[String] = None): OpenApi =
    copy(servers = servers :+ Server(url, description))

  /** Adds a global tag, which can be referenced by its `name` in several locations
    *
    * Example:
    * {{{
    * // used for the doc() shorthand to create ExternalDocumentation object
    * import ch.acmesoftware.typeswagger.v3.OpenApi._
    *
    * OpenApi.create("Test Spec", "1.0.0").
    *   withTag("minimal").
    *   withTag("complete", description = "Tag Description",
    *           externalDocs = doc("http://foo.tld/doc", "Full  documentation"))
    * }}}
    *
    * @param name The name (identifier)
    * @param description A maybe longer, optional description
    * @param externalDocs Link to external docs
    */
  def withTag(name: String, description: Option[String] = None, externalDocs: Option[ExternalDoc] = None): OpenApi =
    copy(tags = tags :+ Tag(name, description, externalDocs))

  /** Adds an API definition for a certain path
    *
    * @param path The relative path of the API endpoint (e.g. /api/user/{id})
    * @param summary A short summary describing the path
    * @param description A longer, optional description
    * @param servers
    * @param parameters
    * @param ops One or many HTTP operations which worh for this path
    */
  def path(path: String, summary: Option[String] = None, description: Option[String] = None, servers: Seq[Server] = Nil, parameters: Seq[Parameter] = Nil)
          (ops: Seq[HttpOperation]): OpenApi =
    copy(paths = paths :+ Path(path, summary, description, servers, parameters, ops.toMap))

  /** Returns the finished API spec as a JSON string */
  def toJson(): String = Json(this)
}

object OpenApi {

  type HttpOperation = (Method, Operation)

  /** Creates a v3 API specification
    *
    * @param title Title of the spec
    * @param version Version of your API (not the specification standard version)
    * @param description A more or less verbose description what the specified API does
    * @param termsOfService A link to your TOS
    * @return A fluid API builder, which can be used to add, servers, tags, paths, etc.
    */
  def create(title: String, version: String, description: Option[String] = None, termsOfService: Option[String] = None): OpenApi =
    OpenApi(info = Info(title = title, version = version, description = description, termsOfService = termsOfService))

  /** Convenience method to create ExternalDoc objects
    *
    * @param url URL of the external documentation
    * @param description An optional description
    */
  def doc(url: String, description: Option[String] = None): ExternalDoc = ExternalDoc(url, description)

  /** Convenience method to create path operations
    *
    * This method is intended to be used together with a [[Method]] reference in a path definition.
    * See the following example:
    * {{{
    * OpenApi.create("ApiDoc", "1.0.0").
    *   path("/user/{id}") {
    *     (POST >> op("A http post call") ~
    *     (GET >> op("A summary", "get operation for this route").
    *       withParameter("id", PATH, Schema.int, description = "The id...", required = true).
    *       withParameter("comment", QUERY, Schema.string, description = "Some deprecated comment", deprecated = true, allowEmptyValue = false).
    *       withTag("testtag"))
    *   }
    * }}}
    *
    * Optional parameters can be provided directly to this method or using the builder methods `withParameters`, `withTag`, etc.
    *
    * @param summary A short summary of the operation
    * @param description A longer description
    * @param operationId A globally unique id for referencing the operation
    * @param tags A list of tags. THere are only string references to the globally defined tags (see [[OpenApi.withTag]])
    * @param externalDocs A link to external documentation. Use [[OpenApi.doc]] shorthand.
    */
  def op(summary: Option[String] = None, description: Option[String] = None, operationId: Option[String] = None,
         tags: Seq[String] = Nil, externalDocs: Option[ExternalDoc] = None) =
    Operation(summary, description, operationId, tags, externalDocs, parameters = Nil, requestBody = None,
      responses = Map(DEFAULT -> Response("Default Response")), callbacks = Nil, servers = Nil, security = Nil, deprecated = false)


  //TODO: complete
  /** Defines possible http verbs (methods) */
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
  /** Defines possible http return status codes */
  sealed trait StatusCode {
    def str(): String
  }

  case object DEFAULT extends StatusCode {
    override def str(): String = "default"
  }

  case object OK extends StatusCode {
    override def str(): String = "200"
  }

  /** Defines the location (field `in` of a parameter
    *
    * @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#parameterObject
    */
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

  /** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#schemaObject */
  case class Schema(schemaType: String, format: Option[String])

  /** Companion defining commen schemas */
  object Schema {
    def string = Schema("string", None)

    def int = Schema("integer", Some("int32"))
  }

}

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#infoObject */
case class Info(title: String, description: Option[String] = None, termsOfService: Option[String] = None, contact: Option[Contact] = None, license: Option[License] = None, version: String)

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#contactObject */
case class Contact(name: Option[String], url: Option[String], email: Option[String])

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#licenseObject*/
case class License(name: Option[String], url: Option[String])

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#serverObject */
case class Server(url: String, description: Option[String] = None) //TODO: add variables: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#serverVariableObject

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#tag-object */
case class Tag(name: String, description: Option[String], externalDocs: Option[ExternalDoc] = None)

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#externalDocumentationObject */
case class ExternalDoc(url: String, description: Option[String] = None)

/** Representing a combination of a Path and a PathItem object
  *
  * @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#pathsObject
  * @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#pathItemObject
  */
case class Path(path: String, summary: Option[String], description: Option[String], servers: Seq[Server], parameters: Seq[Parameter],
                operations: Map[Method, Operation])

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#operationObject */
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

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#parameterObject */
case class Parameter(name: String, in: ParameterLocation, schema: Schema, description: Option[String], required: Boolean,
                     deprecated: Boolean, allowEmptyValue: Boolean)

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#requestBodyObject */
case class RequestBody(content: Seq[MediaType], description: Option[String], required: Boolean)

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#responseObject */
case class Response(description: String)

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#callback-object */
case class Callback()

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#security-requirement-object */
case class SecurityRequirement()

/** @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#mediaTypeObject */
case class MediaType(mimeType: String, schema: Schema, example: Option[String])