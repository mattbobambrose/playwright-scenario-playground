package com.mattbobambrose.examples.scenarios

import com.mattbobambrose.examples.BasePageTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith

class CartFlowTest : BasePageTest("/catalog") {
  init {
    "Add a book from its detail page lands on the cart" {
      page.navigate("/books/1")
      page.locator("[data-testid=add-to-cart]").click()
      page.waitForURL("**/cart")

      page.url() shouldEndWith "/cart"
      page.locator("[data-testid=cart-row-1]").isVisible shouldBe true
      page.locator("[data-testid=cart-subtotal-1]").textContent() shouldContain "\$14.99"
      page.locator("[data-testid=cart-total]").textContent() shouldContain "\$14.99"
      page.locator("[data-testid=cart-count]").textContent() shouldBe "1"
    }

    "Updating quantity recomputes subtotal and total" {
      page.navigate("/books/1")
      page.locator("[data-testid=add-to-cart]").click()
      page.waitForURL("**/cart")

      page.locator("[data-testid=cart-qty-1]").fill("3")
      page.locator("[data-testid=cart-update-1]").click()
      page.waitForURL("**/cart")

      page.locator("[data-testid=cart-qty-1]").inputValue() shouldBe "3"
      page.locator("[data-testid=cart-subtotal-1]").textContent() shouldContain "\$44.97"
      page.locator("[data-testid=cart-total]").textContent() shouldContain "\$44.97"
      page.locator("[data-testid=cart-count]").textContent() shouldBe "3"
    }

    "Adding a second book shows both rows and a combined total" {
      page.navigate("/books/1")
      page.locator("[data-testid=add-to-cart]").click()
      page.waitForURL("**/cart")

      page.navigate("/books/2")
      page.locator("[data-testid=add-to-cart]").click()
      page.waitForURL("**/cart")

      page.locator("[data-testid=cart-row-1]").isVisible shouldBe true
      page.locator("[data-testid=cart-row-2]").isVisible shouldBe true
      page.locator("[data-testid=cart-total]").textContent() shouldContain "\$33.98"
      page.locator("[data-testid=cart-count]").textContent() shouldBe "2"
    }

    "Removing the last row reveals the empty state" {
      page.navigate("/books/1")
      page.locator("[data-testid=add-to-cart]").click()
      page.waitForURL("**/cart")

      page.locator("[data-testid=cart-remove-1]").click()
      page.waitForURL("**/cart")

      page.locator("[data-testid=cart-row-1]").isVisible shouldBe false
      page.locator("[data-testid=cart-empty]").isVisible shouldBe true
      page.locator("[data-testid=cart-empty]").textContent() shouldContain "Your cart is empty."
      page.locator("[data-testid=cart-count]").textContent() shouldBe "0"
    }
  }
}
