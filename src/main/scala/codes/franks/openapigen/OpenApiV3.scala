package codes.franks.openapigen

import codes.franks.openapigen.OpenApiV3.JsonProducer
import org.json4s.JsonAST
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

case class OpenApiV3(openapi: String = "3.0.0", info: Info, servers: Seq[Server] = Nil, tags: Seq[Tag] = Nil) {

  def withVersion(version: String): OpenApiV3 = copy(info = info.copy(version = version))

  def withLicense(name: String, url: Option[String] = None): OpenApiV3 =
    copy(info = info.copy(license = Some(License(Some(name), url))))

  def withContact(name: String, url: Option[String] = None, email: Option[String] = None): OpenApiV3 =
    copy(info = info.copy(contact = Some(Contact(Some(name), url, email))))

  def withServer(url: String, description: Option[String] = None): OpenApiV3 =
    copy(servers = servers :+ Server(url, description))

  def withTag(name: String, description: Option[String], externalDocs: Option[ExternalDoc] = None): OpenApiV3 =
    copy(tags = tags :+ Tag(name, description, externalDocs))

  def toJson(): String = pretty(render(JsonProducer.produce(this)))
}

object OpenApiV3 {
  def create(title: String, version: String, description: Option[String] = None, termsOfService: Option[String] = None): OpenApiV3 =
    OpenApiV3(info = Info(title = title, version = version, description = description, termsOfService = termsOfService))

  object JsonProducer {
    def produce(in: OpenApiV3): JsonAST.JObject = ("openapi" -> in.openapi) ~
      ("info" -> produce(in.info)) ~
      ("servers" -> produce(in.servers))

    def produce(info: Info): JsonAST.JObject = ("title" -> info.title) ~
      ("version" -> info.version) ~
      ("description" -> info.description) ~
      ("termsOfService" -> info.termsOfService) ~
      ("license" -> info.license.map(l => ("name" -> l.name) ~ ("url" -> l.url))) ~
      ("contact" -> info.contact.map(c => ("name" -> c.name) ~ ("url" -> c.url) ~ ("email" -> c.email)))

    def produce(in: Seq[Server]): Seq[JsonAST.JObject] = in.map(s => ("url" -> s.url) ~
      ("description" -> s.description)
    )
  }

}

case class Info(title: String, description: Option[String] = None, termsOfService: Option[String] = None, contact: Option[Contact] = None, license: Option[License] = None, version: String)

case class Contact(name: Option[String], url: Option[String], email: Option[String])

case class License(name: Option[String], url: Option[String])

case class Server(url: String, description: Option[String] = None) //TODO: add variables: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#serverVariableObject

case class Tag(name: String, description: Option[String], externalDocs: Option[ExternalDoc] = None)

case class ExternalDoc(url: String, description: Option[String] = None)

