package com.mattbobambrose.examples.scenarios

import com.mattbobambrose.examples.BasePageTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class LoginHappyAndUnhappyTest : BasePageTest("/login") {
  init {
    "Invalid credentials re-render the form with an error" {
      page.locator("[data-testid=input-username]").fill("alice")
      page.locator("[data-testid=input-password]").fill("wrongpass")
      page.locator("[data-testid=login-submit]").click()
      page.waitForURL("**/login")

      page.locator("[data-testid=flash-error]").textContent() shouldContain "Invalid username or password."
      page.locator("[data-testid=nav-login]").isVisible shouldBe true
    }

    "Valid credentials land on the home page with an authenticated header" {
      page.locator("[data-testid=input-username]").fill("alice")
      page.locator("[data-testid=input-password]").fill("wonderland")
      page.locator("[data-testid=login-submit]").click()
      page.waitForURL("**/")

      page.locator("[data-testid=nav-user]").waitFor()
      page.locator("[data-testid=nav-user]").textContent() shouldContain "Hi, alice"
      page.locator("[data-testid=nav-logout]").isVisible shouldBe true
      page.locator("[data-testid=nav-shelves]").getAttribute("href") shouldContain "/shelves"
      page.locator("[data-testid=nav-orders]").getAttribute("href") shouldContain "/orders"
      page.locator("[data-testid=nav-login]").isVisible shouldBe false
    }
  }
}
