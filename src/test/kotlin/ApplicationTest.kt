package com.mattbobambrose

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

class ApplicationTest : StringSpec() {
  init {
    "home page renders hero" {
      runTestApp { client ->
        val res = client.get("/")
        res.status shouldBe HttpStatusCode.OK
        res.bodyAsText() shouldContain "Your next favorite book is on the shelf."
      }
    }

    "about page renders demo accounts" {
      runTestApp { client ->
        val res = client.get("/about")
        res.status shouldBe HttpStatusCode.OK
        res.bodyAsText() shouldContain "demo / demo"
      }
    }

    "unknown route returns 404" {
      runTestApp { client ->
        client.get("/no-such-page").status shouldBe HttpStatusCode.NotFound
      }
    }
  }
}
