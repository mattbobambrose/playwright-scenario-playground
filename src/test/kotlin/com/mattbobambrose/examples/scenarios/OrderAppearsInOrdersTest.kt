package com.mattbobambrose.examples.scenarios

import com.mattbobambrose.examples.BasePageTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith

class OrderAppearsInOrdersTest : BasePageTest("/login") {
  init {
    "Placing an order lists it on the Orders page" {
      // Log in as alice
      page.locator("[data-testid=input-username]").fill("alice")
      page.locator("[data-testid=input-password]").fill("wonderland")
      page.locator("[data-testid=login-submit]").click()
      page.locator("[data-testid=nav-user]").waitFor()
      page.waitForURL("**/")

      // Navigate to book 5
      page.navigate("/books/5")
      page.waitForLoadState()
      page.locator("[data-testid=book-title]").textContent() shouldContain "A Quiet Revolution"

      // Add to cart
      page.locator("[data-testid=add-to-cart]").click()
      page.waitForURL("**/cart")
      page.locator("h1").textContent() shouldContain "Your Cart"
      page.locator("[data-testid=cart-table]").textContent() shouldContain "A Quiet Revolution"

      // Go to checkout
      page.navigate("/checkout")
      page.waitForLoadState()
      page.locator("h1").textContent() shouldContain "Checkout"

      // Fill checkout form
      page.locator("[data-testid=input-name]").fill("Alice Liddell")
      page.locator("[data-testid=input-email]").fill("alice@example.com")
      page.locator("[data-testid=input-address]").fill("7 Rabbit Hole Ln")
      page.locator("[data-testid=input-cardNumber]").fill("4111111111111111")
      page.locator("[data-testid=input-cardExpiry]").fill("04/30")
      page.locator("[data-testid=input-cardCvc]").fill("123")

      // Place order
      page.locator("[data-testid=place-order]").click()
      page.waitForURL("**/checkout/confirmation/ORD-*")

      // Confirmation page
      page.locator("h1").textContent() shouldContain "Thank you for your order!"
      page.locator("[data-testid=confirmation-id]").textContent() shouldContain "ORD-"

      // Navigate to orders
      page.navigate("/orders")
      page.waitForLoadState()
      page.locator("h1").textContent() shouldContain "Your orders"
      page.locator("[data-testid=orders-empty]").isVisible shouldBe false
      page.locator("[data-testid=orders-list]").textContent() shouldContain "ORD-"
      page.locator("[data-testid=orders-list]").textContent() shouldContain "A Quiet Revolution × 1"
      page.locator("[data-testid=orders-list]").textContent() shouldContain "$16.99"
    }
  }
}
