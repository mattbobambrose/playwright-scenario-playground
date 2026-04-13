package com.mattbobambrose

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.parameters

class ShelfTest : StringSpec() {
  init {
    "unauthenticated shelves page redirects to login" {
      runTestApp { client ->
        val res = client.get("/shelves")
        res.status shouldBe HttpStatusCode.OK
        res.bodyAsText() shouldContain "Log in"
      }
    }

    "add to shelf lands on book detail with flash" {
      runTestApp {
        val client = testClient()
        client.loginAs("demo", "demo")
        val post = client.submitForm(
          url = "/shelves/add",
          formParameters = parameters {
            append("bookId", "1")
            append("shelf", "reading")
          },
        )
        val res = client.followRedirectAsGet(post)
        res.status shouldBe HttpStatusCode.OK
        res.bodyAsText() shouldContain "Reading added."
      }
    }

    "shelves page shows the added book" {
      runTestApp {
        val client = testClient()
        client.loginAs("demo", "demo")
        client.submitForm(
          url = "/shelves/add",
          formParameters = parameters {
            append("bookId", "1")
            append("shelf", "want")
          },
        )
        val body = client.get("/shelves").bodyAsText()
        body shouldContain "The Silent Ring"
        body shouldContain "data-shelf=\"WANT\""
      }
    }

    "move endpoint moves a book between shelves" {
      runTestApp {
        val client = testClient()
        client.loginAs("demo", "demo")
        client.submitForm(
          url = "/shelves/add",
          formParameters = parameters {
            append("bookId", "2")
            append("shelf", "want")
          },
        )
        val res = client.post("/shelves/move") {
          contentType(ContentType.Application.Json)
          setBody("""{"bookId":2,"fromShelf":"WANT","toShelf":"READING"}""")
        }
        res.status shouldBe HttpStatusCode.NoContent

        val body = client.get("/shelves").bodyAsText()
        val readingSectionIndex = body.indexOf("data-testid=\"shelf-reading\"")
        val wantSectionIndex = body.indexOf("data-testid=\"shelf-want\"")
        val finishedSectionIndex = body.indexOf("data-testid=\"shelf-finished\"")
        readingSectionIndex shouldNotBe -1
        val reading = body.substring(readingSectionIndex, finishedSectionIndex)
        reading shouldContain "Echoes of Mars"
        val want = body.substring(wantSectionIndex, readingSectionIndex)
        want.contains("Echoes of Mars") shouldBe false
      }
    }

    "reorder endpoint accepts valid payload" {
      runTestApp {
        val client = testClient()
        client.loginAs("demo", "demo")
        client.submitForm(
          url = "/shelves/add",
          formParameters = parameters { append("bookId", "1"); append("shelf", "reading") },
        )
        client.submitForm(
          url = "/shelves/add",
          formParameters = parameters { append("bookId", "2"); append("shelf", "reading") },
        )
        val res = client.post("/shelves/reorder") {
          contentType(ContentType.Application.Json)
          setBody("""{"shelf":"READING","bookId":2,"beforeBookId":1}""")
        }
        res.status shouldBe HttpStatusCode.NoContent
      }
    }

    "move endpoint rejects invalid shelf" {
      runTestApp {
        val client = testClient()
        client.loginAs("demo", "demo")
        val res = client.post("/shelves/move") {
          contentType(ContentType.Application.Json)
          setBody("""{"bookId":1,"fromShelf":"WAT","toShelf":"READING"}""")
        }
        res.status shouldBe HttpStatusCode.BadRequest
      }
    }
  }

}
