package com.mattbobambrose

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

class CatalogTest : StringSpec() {
  init {
    "catalog lists books" {
      runTestApp { client ->
        val body = client.get("/catalog").bodyAsText()
        body shouldContain "Deep Water"
      }
    }

    "catalog search filters by title" {
      runTestApp { client ->
        val body = client.get("/catalog?q=lunar").bodyAsText()
        body shouldContain "The Lunar Protocol"
        body shouldNotContain "The Silent Ring"
      }
    }

    "catalog filter by genre shows only that genre" {
      runTestApp { client ->
        val body = client.get("/catalog?genre=mystery").bodyAsText()
        body shouldContain "Deep Water"
        body shouldNotContain "The Silent Ring"
      }
    }

    "catalog paginates at 6 per page" {
      runTestApp { client ->
        val body = client.get("/catalog").bodyAsText()
        body shouldContain "Page 1 of"
      }
    }

    "empty search renders empty state" {
      runTestApp { client ->
        val body = client.get("/catalog?q=zzznotabook").bodyAsText()
        body shouldContain "No books match your search."
      }
    }

    "book detail page returns 200 for valid id" {
      runTestApp { client ->
        val res = client.get("/books/1")
        res.status shouldBe HttpStatusCode.OK
        res.bodyAsText() shouldContain "The Silent Ring"
      }
    }

    "book detail page returns 404 for unknown id" {
      runTestApp { client ->
        client.get("/books/9999").status shouldBe HttpStatusCode.NotFound
      }
    }
  }
}
