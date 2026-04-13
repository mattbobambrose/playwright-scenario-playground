package com.mattbobambrose

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters

class AuthTest : StringSpec() {
  init {
    "login page renders form" {
      runTestApp { client ->
        val body = client.get("/login").bodyAsText()
        body shouldContain "Log in"
      }
    }

    "invalid credentials re-render with error" {
      runTestApp { client ->
        val res = client.submitForm(
          url = "/login",
          formParameters = parameters {
            append("username", "demo")
            append("password", "wrong")
            append("next", "/")
          },
        )
        res.status shouldBe HttpStatusCode.OK
        res.bodyAsText() shouldContain "Invalid username or password."
      }
    }

    "valid credentials redirect and set session" {
      runTestApp { client ->
        client.loginAs("demo", "demo")
        val orders = client.get("/orders")
        orders.status shouldBe HttpStatusCode.OK
        orders.bodyAsText() shouldContain "Your orders"
      }
    }
  }
}
