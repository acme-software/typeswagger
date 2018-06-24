package ch.acmesoftware.typeswagger.v3

import ch.acmesoftware.typeswagger.v3.Implicits._
import ch.acmesoftware.typeswagger.v3.OpenApi._
import org.scalatest.{FlatSpec, Matchers}

class OpenApiTest extends FlatSpec with Matchers {

  "OpenApiV3" should "be created with required minimal information" in {
    val spec = OpenApi.create("Test Spec", "1.0.0")
    val json = spec.toJson()

    //println(json)
  }

  it should "print json for a complex example" in {
    val spec = OpenApi.create("ApiDoc", "1.0.0", description = "An API Documentation built with TypeSwagger", termsOfService = "/tos").
      withInfo(license = "MIT", licenseUrl = "https://opensource.org/licenses/MIT").
      withTag("testtag", "A tag description", externalDocs = doc("http://link.to.doc")).
      path("/user/{id}") {
        (GET >> op("A summary", "GET operation for this route").
          withParameter("id", PATH, Schema.int, description = "The id...", required = true).
          withParameter("comment", QUERY, Schema.string, description = "Some deprecated comment", deprecated = true).
          withTag("testtag")) ~
        (DELETE >> op("Delete operation").
          withParameter("id", PATH, Schema.int, description = "The id...", required = true))
      }.
      withServer("http://localhost:9000/api", Some("Local Dev Server")).
      withServer("https://production.tld/api", Some("Production Server"))

    val json = spec.toJson()

    println(json)
  }
}
