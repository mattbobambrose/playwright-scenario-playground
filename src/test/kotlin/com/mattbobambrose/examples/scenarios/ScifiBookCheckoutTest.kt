package com.mattbobambrose.examples.scenarios

import com.mattbobambrose.examples.BasePageTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class ScifiBookCheckoutTest : BasePageTest("/") {
  init {
    "Filter sci-fi books and reach checkout" {
      val landingBody = page.locator("body").textContent()
      landingBody shouldContain "Your next favorite book is on the shelf."
      landingBody shouldContain "Browse the catalog"
      landingBody shouldContain "Log in"

      page.locator("[data-testid=cta-browse]").click()
      page.waitForURL("**/catalog")
      page.locator("h1").textContent() shouldBe "Catalog"

      page.locator("[data-testid=filter-genre]").selectOption("scifi")
      page.locator("[data-testid=filter-sort]").selectOption("priceAsc")
      page.locator("[data-testid=filter-apply]").click()
      page.waitForURL("**/catalog?**genre=scifi**")

      val firstBookLink = page.locator("main a[href^='/books/']").first()
      firstBookLink.textContent() shouldContain "Orbital"

      firstBookLink.click()
      page.waitForURL("**/books/13")
      page.locator("h1").textContent() shouldBe "Orbital"

      page.locator("[data-testid=add-to-cart]").click()
      page.waitForURL("**/cart")
      page.locator("h1").textContent() shouldBe "Your Cart"
      page.locator("body").textContent() shouldContain "Orbital"
      page.locator("body").textContent() shouldContain "$15.99"

      page.locator("[data-testid=checkout-button]").click()
      page.waitForURL("**/checkout")
      page.locator("h1").textContent() shouldBe "Checkout"
      page.locator("body").textContent() shouldContain "Orbital × 1"
    }
  }
}
