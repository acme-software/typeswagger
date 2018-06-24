TypeSwagger - A Swagger / OpenApi Spec DSL for Scala
====================================================

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Build Status](https://travis-ci.org/acme-software/typeswagger.svg?branch=master)](https://travis-ci.org/acme-software/typeswagger)

**TypeSwagger provides a typesafe Scala DSL to build OpenApi (Swagger) Specifications. It can be used within any Scala 
project to generate HTTP API documentations**

## Example

The following example defines a simple api with two endpoints (`GET` and `DELETE`) on one path (`/user/{id}`). The 
example contains mandatory information, and also some, but not all optional fields. For better readability, optional 
fields are calld by value.

```scala
import ch.acmesoftware.typeswagger.v3.OpenApi
import ch.acmesoftware.typeswagger.v3.OpenApi._
import ch.acmesoftware.typeswagger.v3.Implicits._

OpenApi.create("ApiDoc", "1.0.0").
  // add global information
  withInfo(license = "MIT", licenseUrl = "https://opensource.org/licenses/MIT").
  // add a tag
  withTag("testtag", "A tag description", externalDocs = doc("http://link.to.doc")).
  // define an api path
  path("/user/{id}", summary = "Path summary") {
    // http GET operation  on the path
    (GET >> op("A summary", "GET operation for this route").
      withParameter("id", PATH, Schema.int, description = "The id...", required = true).
      withParameter("comment", QUERY, Schema.string, description = "Some deprecated comment", deprecated = true).
      withTag("testtag")) ~
    // http DELETE operation on the path
    (DELETE >> op("Delete operation").
      withParameter("id", PATH, Schema.int, description = "The id...", required = true))
  }.
  // add endpoint server(s)
  withServer("http://localhost:9000/api").
  withServer("https://production.tld/api", Some("Production Server")).
  // build json string
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

// syntactic sugar for convenient DSL, but optional
import ch.acmesoftware.typeswagger.v3.Implicits._ 
```

### Info

Add global information using the `withInfo` function. All parameters are optional. If not using the implicits, 
use `Some("str")` as parameter value:

```scala
import ch.acmesoftware.typeswagger.v3.OpenApi
import ch.acmesoftware.typeswagger.v3.OpenApi._
import ch.acmesoftware.typeswagger.v3.Implicits._

OpenApi.create("ApiDoc", "1.0.0", description = "API Doc built with TypeSwagger", termsOfService = "/tos").
  withInfo(license = "MIT", licenseUrl = "https://opensource.org/licenses/MIT", 
           contactName = "John Doe", contactEmail = "info@server.tld", contactUrl = "https://webpage.tld")
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