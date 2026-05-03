package com.mattbobambrose.examples.scenarios

import com.mattbobambrose.examples.BasePageTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith

class CheckoutFlowTest : BasePageTest("/catalog") {
  init {
    "Empty submit surfaces all six field-level errors" {
      page.navigate("/books/1")
      page.locator("[data-testid=add-to-cart]").click()
      page.waitForURL("**/cart")

      page.locator("[data-testid=checkout-button]").click()
      page.waitForURL("**/checkout")
      page.url() shouldEndWith "/checkout"

      page.locator("[data-testid=place-order]").click()
      page.waitForURL("**/checkout")
      page.url() shouldEndWith "/checkout"

      page.locator("body").textContent() shouldContain "Please enter your full name."
      page.locator("body").textContent() shouldContain "Email is required."
      page.locator("body").textContent() shouldContain "Shipping address is required."
      page.locator("body").textContent() shouldContain "Card number is required."
      page.locator("body").textContent() shouldContain "Expiry is required."
      page.locator("body").textContent() shouldContain "CVC is required."
      page.locator("[data-field-error=true]").count() shouldBe 6
    }

    "Format-level errors call out bad email, card, expiry, and CVC values" {
      page.navigate("/books/1")
      page.locator("[data-testid=add-to-cart]").click()
      page.waitForURL("**/cart")

      page.navigate("/checkout")
      page.waitForURL("**/checkout")

      page.locator("[data-testid=input-name]").fill("Ada Lovelace")
      page.locator("[data-testid=input-email]").fill("foo@bar")
      page.locator("[data-testid=input-address]").fill("10 Downing St")
      page.locator("[data-testid=input-cardNumber]").fill("1234")
      page.locator("[data-testid=input-cardExpiry]").fill("13/99")
      page.locator("[data-testid=input-cardCvc]").fill("ab")
      page.locator("[data-testid=place-order]").click()
      page.waitForURL("**/checkout")

      page.url() shouldEndWith "/checkout"
      page.locator("body").textContent() shouldContain "That doesn't look like a valid email."
      page.locator("body").textContent() shouldContain "Card number must be 13\u201319 digits."
      page.locator("body").textContent() shouldContain "Use MM/YY format."
      page.locator("body").textContent() shouldContain "CVC must be 3 or 4 digits."
    }

    "Valid checkout lands on the confirmation page" {
      page.navigate("/books/1")
      page.locator("[data-testid=add-to-cart]").click()
      page.waitForURL("**/cart")

      page.navigate("/checkout")
      page.waitForURL("**/checkout")

      page.locator("[data-testid=input-name]").fill("Ada Lovelace")
      page.locator("[data-testid=input-email]").fill("ada@example.com")
      page.locator("[data-testid=input-address]").fill("10 Downing St")
      page.locator("[data-testid=input-cardNumber]").fill("4111111111111111")
      page.locator("[data-testid=input-cardExpiry]").fill("04/30")
      page.locator("[data-testid=input-cardCvc]").fill("123")
      page.locator("[data-testid=place-order]").click()
      page.waitForURL("**/checkout/confirmation/**")

      page.url() shouldContain "/checkout/confirmation/"
      page.locator("[data-testid=confirmation]").isVisible shouldBe true
      page.locator("[data-testid=confirmation-id]").textContent() shouldNotBe ""
      page.locator("body").textContent() shouldContain "Thank you for your order!"

      page.navigate("/cart")
      page.waitForURL("**/cart")
      page.locator("[data-testid=cart-empty]").isVisible shouldBe true
    }
  }
}
