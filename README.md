TypeSwagger - A Swagger / OpenApi Specification DSL for Scala
=============================================================

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**TypeSwagger provides a typesafe Scala DSL to build OpenApi (Swagger) Specifications. It can be used within any Scala 
project to generate HTTP API documentations**

## Example

```scala
import ch.acmesoftware.typeswagger.v3.OpenApi
import ch.acmesoftware.typeswagger.v3.OpenApi._
import ch.acmesoftware.typeswagger.v3.Implicits._

OpenApi.create("ApiDoc", "1.0.0", description = "An API Documentation built with TypeSwagger", termsOfService = "/tos").
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
  withServer("https://production.tld/api", Some("Production Server")).
  toJson()
```

## Usage

Install the Ivy depenency via SBT:

```scala
"ch.acmesoftware" %% "typeswagger" % "{version}"
```

Add imports:

```scala
import ch.acmesoftware.typeswagger.v3.OpenApi
import ch.acmesoftware.typeswagger.v3.OpenApi._

// syntactic sugar fpr convenient DSL, but optional
import ch.acmesoftware.typeswagger.v3.Implicits._ 
```

## Features

* Convenient, typesafe DSL to define OpenApi v3 specifications
* Integrated JSON rendering
* Integrated YAML rendering (TODO)
* Java API (TODO)
* Integration with common HTTP-Frameworks (TODO)

## Contributions

Please use the GitHub issue tracking and PullRequests. Any help is very welcome.

## License

This project is licensed under the MIT license. See [LICENSE](./LICENSE) for more information.