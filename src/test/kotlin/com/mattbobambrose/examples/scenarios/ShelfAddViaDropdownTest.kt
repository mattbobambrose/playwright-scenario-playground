package com.mattbobambrose.examples.scenarios

import com.mattbobambrose.examples.BasePageTest
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class ShelfAddViaDropdownTest : BasePageTest("/login") {
  init {
    "Adding The Lunar Protocol to the Finished shelf" {
      // Log in as alice
      page.locator("[data-testid=input-username]").fill("alice")
      page.locator("[data-testid=input-password]").fill("wonderland")
      page.locator("[data-testid=login-submit]").click()
      page.locator("[data-testid=nav-user]").waitFor()
      page.waitForURL("**/")

      // Navigate to book 8
      page.navigate("/books/8")
      page.waitForLoadState()
      page.locator("[data-testid=book-title]").textContent() shouldContain "The Lunar Protocol"

      // Verify shelf dropdown options
      val options = page.locator("[data-testid=shelf-select] option").allTextContents()
      options shouldContainAll listOf("Want to Read", "Reading", "Finished")

      // Select "Finished" and add to shelf
      page.selectOption("[data-testid=shelf-select]", "finished")
      page.locator("[data-testid=add-to-shelf]").click()
      page.waitForURL("**/books/8?added=Finished")
      page.locator("[data-testid=flash-success]").textContent() shouldContain "Finished added."

      // Navigate to shelves
      page.navigate("/shelves")
      page.waitForLoadState()

      // Finished shelf has the book with count 1
      page.locator("[data-testid=shelf-count-finished]").textContent() shouldContain "1"
      page.locator("[data-testid=shelf-finished]").textContent() shouldContain "The Lunar Protocol"

      // Want to Read and Reading are empty
      page.locator("[data-testid=shelf-count-want]").textContent() shouldContain "0"
      page.locator("[data-testid=shelf-empty-want]").textContent() shouldContain "Drop a book here"
      page.locator("[data-testid=shelf-count-reading]").textContent() shouldContain "0"
      page.locator("[data-testid=shelf-empty-reading]").textContent() shouldContain "Drop a book here"
    }
  }
}
