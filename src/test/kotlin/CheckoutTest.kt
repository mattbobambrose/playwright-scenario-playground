package com.mattbobambrose

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters

class CheckoutTest : StringSpec() {
  init {
    "empty checkout redirects to cart" {
      runTestApp { client ->
        val res = client.get("/checkout")
        res.status shouldBe HttpStatusCode.OK
        res.bodyAsText() shouldContain "Your cart is empty."
      }
    }

    "invalid form renders field errors" {
      runTestApp {
        val client = testClient()
        client.submitForm(
          url = "/cart/add",
          formParameters = parameters { append("bookId", "1") },
        )
        val res = client.submitForm(
          url = "/checkout",
          formParameters = parameters {
            append("name", "")
            append("email", "not-an-email")
            append("address", "")
            append("cardNumber", "abc")
            append("cardExpiry", "13/99")
            append("cardCvc", "12")
          },
        )
        res.status shouldBe HttpStatusCode.BadRequest
        val body = res.bodyAsText()
        body shouldContain "Please enter your full name."
        body shouldContain "That doesn't look like a valid email."
        body shouldContain "Shipping address is required."
        body shouldContain "Card number must be 13–19 digits."
        body shouldContain "Use MM/YY format."
        body shouldContain "CVC must be 3 or 4 digits."
      }
    }

    "valid checkout clears cart and creates order" {
      runTestApp {
        val client = testClient()
        client.loginAs("demo", "demo")
        client.submitForm(
          url = "/cart/add",
          formParameters = parameters { append("bookId", "1") },
        )
        val post = client.submitForm(
          url = "/checkout",
          formParameters = parameters {
            append("name", "Demo Reader")
            append("email", "demo@example.com")
            append("address", "1 Test Lane")
            append("cardNumber", "4111111111111111")
            append("cardExpiry", "12/30")
            append("cardCvc", "123")
          },
        )
        val res = client.followRedirectAsGet(post)
        res.status shouldBe HttpStatusCode.OK
        res.bodyAsText() shouldContain "Thank you for your order!"

        val orders = client.get("/orders").bodyAsText()
        orders shouldContain "ORD-"

        val cart = client.get("/cart").bodyAsText()
        cart shouldContain "Your cart is empty."
      }
    }
  }
}
