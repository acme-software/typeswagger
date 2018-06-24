package ch.acmesoftware.typeswagger.v3

import ch.acmesoftware.typeswagger.v3.Implicits._
import ch.acmesoftware.typeswagger.v3.OpenApi._
import org.scalatest.{FlatSpec, Matchers}
import org.json4s._
import org.json4s.native.JsonMethods._

class OpenApiTest extends FlatSpec with Matchers {

  implicit val formats = DefaultFormats

  "OpenApiV3" should "be created with required minimal information" in {
    val res = parse(OpenApi.create("Test Spec", "1.0.0").
      toJson())

    (res \ "openapi").extract[String] shouldEqual "3.0.0"
    (res \ "info" \ "title").extract[String] shouldEqual "Test Spec"
    (res \ "info" \ "version").extract[String] shouldEqual "1.0.0"
  }

  it should "provide global license information" in {
    val res = parse(OpenApi.create("Test Spec", "1.0.0").
      withInfo(license = "MIT", licenseUrl = "https://opensource.org/licenses/MIT").
      toJson())

    (res \ "info" \ "license" \ "name").extract[String] shouldEqual "MIT"
    (res \ "info" \ "license" \ "url").extract[String] shouldEqual "https://opensource.org/licenses/MIT"
  }

  it should "provide global contact information" in {
    val res = parse( OpenApi.create("Test Spec", "1.0.0").
      withInfo(contactName = "John Doe", contactEmail = "info@acmesoftware.ch", contactUrl = "https://www.acmesoftware.ch").
      toJson())

    (res \ "info" \ "contact" \ "name").extract[String] shouldEqual "John Doe"
    (res \ "info" \ "contact" \ "email").extract[String] shouldEqual "info@acmesoftware.ch"
    (res \ "info" \ "contact" \ "url").extract[String] shouldEqual "https://www.acmesoftware.ch"
  }

  it should "add tags" in {
    val res = parse(OpenApi.create("Test Spec", "1.0.0").
      withTag("minimal").
      withTag("complete", description = "Tag Description", externalDocs = doc("http://foo.tld/doc", "Full  documentation")).
      toJson())

    ((res \ "tags")(0) \ "name").extract[String] shouldEqual "minimal"
    ((res \ "tags")(1) \ "name").extract[String] shouldEqual "complete"
    ((res \ "tags")(1) \ "description").extract[String] shouldEqual "Tag Description"
    ((res \ "tags")(1) \ "externalDocs" \ "url").extract[String] shouldEqual "http://foo.tld/doc"
    ((res \ "tags")(1) \ "externalDocs" \ "description").extract[String] shouldEqual "Full  documentation"
  }

  it should "add servers" in {
    val res = parse(OpenApi.create("Test Spec", "1.0.0").
      withServer("http://localhost").
      withServer("http://prod.tld", description = "Production Server").
      toJson())

    ((res \ "servers")(0) \ "url").extract[String] shouldEqual "http://localhost"
    ((res \ "servers")(1) \ "url").extract[String] shouldEqual "http://prod.tld"
    ((res \ "servers")(1) \ "description").extract[String] shouldEqual "Production Server"
  }

  it should "print json for a complex example" in {
    val json = OpenApi.create("ApiDoc", "1.0.0", description = "An API Documentation built with TypeSwagger", termsOfService = "/tos").
      withInfo(license = "MIT", licenseUrl = "https://opensource.org/licenses/MIT").
      withTag("testtag", "A tag description", externalDocs = doc("http://link.to.doc")).
      path("/user/{id}", summary = "Path summary") {
        (GET >> op("A summary", "GET operation for this route").
          withParameter("id", PATH, Schema.int, description = "The id...", required = true).
          withParameter("comment", QUERY, Schema.string, description = "Some deprecated comment", deprecated = true).
          withTag("testtag")) ~
        (DELETE >> op("Delete operation").
          withParameter("id", PATH, Schema.int, description = "The id...", required = true))
      }.
      withServer("http://localhost:9000/api", Some("Local Dev Server")).
      withServer("https://production.tld/api", Some("Production Server")).
      toJson()

    println(json)

    val res = parse(json)

    (res \ "paths" \ "/user/{id}" \ "summary").extract[String] shouldEqual "Path summary"
  }
}
