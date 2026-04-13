package com.mattbobambrose

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters

class CartTest : StringSpec() {
  init {
    "empty cart shows empty state" {
      runTestApp { client ->
        val body = client.get("/cart").bodyAsText()
        body shouldContain "Your cart is empty."
      }
    }

    "adding a book puts it in the cart" {
      runTestApp {
        val client = testClient()
        client.submitForm(
          url = "/cart/add",
          formParameters = parameters { append("bookId", "1") },
        )
        val body = client.get("/cart").bodyAsText()
        body shouldContain "The Silent Ring"
        body shouldContain "Total:"
      }
    }

    "updating qty recalculates subtotal" {
      runTestApp {
        val client = testClient()
        client.submitForm(
          url = "/cart/add",
          formParameters = parameters { append("bookId", "1") },
        )
        client.submitForm(
          url = "/cart/1/qty",
          formParameters = parameters { append("qty", "3") },
        )
        val body = client.get("/cart").bodyAsText()
        body shouldContain "$44.97"
      }
    }

    "removing a book clears the line" {
      runTestApp {
        val client = testClient()
        client.submitForm(
          url = "/cart/add",
          formParameters = parameters { append("bookId", "1") },
        )
        client.submitForm(
          url = "/cart/1/remove",
          formParameters = parameters {},
        )
        val body = client.get("/cart").bodyAsText()
        body shouldContain "Your cart is empty."
      }
    }

    "adding an unknown book returns 400" {
      runTestApp { client ->
        val res = client.submitForm(
          url = "/cart/add",
          formParameters = parameters { append("bookId", "9999") },
        )
        res.status shouldBe HttpStatusCode.BadRequest
      }
    }
  }
}
