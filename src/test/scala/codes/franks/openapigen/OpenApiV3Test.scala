package codes.franks.openapigen

import org.scalatest.{FlatSpec, Matchers}

class OpenApiV3Test extends FlatSpec with Matchers {

  "OpenApiV3" should "be created with required minimal information" in {
    val spec = OpenApiV3.create("Test Spec", "1.0.0")
    val json = spec.toJson()

    println(json)
  }

  it should "print json for a complex example" in {
    val spec = OpenApiV3.create("Test Spec", "1.0.0", Some("Testing the spec DSL"), Some("All MIT licensed")).
      withLicense("MIT", Some("https://opensource.org/licenses/MIT")).
      withContact("Frank", Some("https://franks.codes"), Some("info@franks.codes")).
      withServer("http://localhost:9000/api", Some("Local Dev Server")).
      withServer("https://production.tld/api", Some("Production Server"))
    val json = spec.toJson()

    println(json)
  }
}
