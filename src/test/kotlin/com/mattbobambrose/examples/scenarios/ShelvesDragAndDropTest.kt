package com.mattbobambrose.examples.scenarios

import com.mattbobambrose.examples.BasePageTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith

class ShelvesDragAndDropTest : BasePageTest("/login") {
  init {
    "Logging in as demo lands on the home page with the shelves nav link" {
      login()

      page.url() shouldEndWith "/"
      page.locator("[data-testid=nav-user]").textContent() shouldContain "Hi, demo"
      page.locator("[data-testid=nav-shelves]").isVisible shouldBe true
    }

    "Adding a book to Reading from the book detail page shows a flash" {
      login()
      addToShelf(1, "reading")

      page.url() shouldContain "/books/1?added=Reading"
      page.locator("[data-testid=flash-success]").textContent() shouldContain "Reading added."
    }

    "The shelves page shows each shelf column with a count badge" {
      login()
      addToShelf(1, "want")
      addToShelf(2, "reading")

      page.navigate("/shelves")
      page.waitForURL("**/shelves")

      page.locator("[data-testid=shelf-column-want]").isVisible shouldBe true
      page.locator("[data-testid=shelf-column-reading]").isVisible shouldBe true
      page.locator("[data-testid=shelf-column-finished]").isVisible shouldBe true

      page.locator("[data-testid=shelf-count-want]").textContent() shouldBe "1"
      page.locator("[data-testid=shelf-count-reading]").textContent() shouldBe "1"

      page.locator("[data-testid=shelf-want] [data-testid=shelf-book-1]").isVisible shouldBe true
      page.locator("[data-testid=shelf-reading] [data-testid=shelf-book-2]").isVisible shouldBe true
      page.locator("[data-testid=shelf-empty-finished]").isVisible shouldBe true
      page.locator("[data-testid=shelf-empty-finished]").textContent() shouldContain "Drop a book here"
    }

    "Dragging a book between shelves moves it and updates the counts" {
      login()
      addToShelf(1, "want")

      page.navigate("/shelves")
      page.waitForURL("**/shelves")

      dispatchHtml5Drag(
        "[data-testid=shelf-book-1]",
        "[data-testid=shelf-reading]",
      )

      page.locator("[data-testid=shelf-reading] [data-testid=shelf-book-1]").waitFor()

      page.locator("[data-testid=shelf-reading] [data-testid=shelf-book-1]").isVisible shouldBe true
      page.locator("[data-testid=shelf-count-want]").textContent() shouldBe "0"
      page.locator("[data-testid=shelf-count-reading]").textContent() shouldBe "1"
      page.locator("[data-testid=shelf-empty-want]").isVisible shouldBe true

      page.reload()
      page.waitForLoadState()

      page.locator("[data-testid=shelf-reading] [data-testid=shelf-book-1]").isVisible shouldBe true
    }

    "Reordering within a shelf persists across reloads" {
      login()
      addToShelf(1, "reading")
      addToShelf(2, "reading")
      addToShelf(3, "reading")

      page.navigate("/shelves")
      page.waitForURL("**/shelves")

      dispatchHtml5Drag(
        "[data-testid=shelf-book-3]",
        "[data-testid=shelf-book-1]",
      )

      page.waitForFunction(
        """() => {
          const items = document.querySelectorAll('[data-testid=shelf-reading] li.book-card');
          return items.length >= 3 && items[0].getAttribute('data-testid') === 'shelf-book-3';
        }""",
      )

      page.reload()
      page.waitForLoadState()

      val items = page.locator("[data-testid=shelf-reading] li.book-card")
      items.first().getAttribute("data-testid") shouldBe "shelf-book-3"
      items.nth(2).getAttribute("data-testid") shouldBe "shelf-book-2"
    }
  }

  private fun login() {
    page.locator("[data-testid=input-username]").fill("demo")
    page.locator("[data-testid=input-password]").fill("demo")
    page.locator("[data-testid=login-submit]").click()
    page.locator("[data-testid=nav-user]").waitFor()
  }

  private fun addToShelf(bookId: Int, shelf: String) {
    page.navigate("/books/$bookId")
    page.selectOption("[data-testid=shelf-select]", shelf)
    page.locator("[data-testid=add-to-shelf]").click()
    page.waitForURL("**/books/$bookId**")
  }

  private fun dispatchHtml5Drag(sourceSelector: String, targetSelector: String) {
    page.evaluate(
      """({sourceSelector, targetSelector}) => {
        const source = document.querySelector(sourceSelector);
        const target = document.querySelector(targetSelector);
        if (!source || !target) throw new Error('Missing source or target');
        const dataTransfer = new DataTransfer();
        const rect = target.getBoundingClientRect();
        const clientY = rect.top + 2;
        const clientX = rect.left + rect.width / 2;
        source.dispatchEvent(new DragEvent('dragstart', { bubbles: true, cancelable: true, dataTransfer }));
        target.dispatchEvent(new DragEvent('dragover',  { bubbles: true, cancelable: true, dataTransfer, clientX, clientY }));
        target.dispatchEvent(new DragEvent('drop',      { bubbles: true, cancelable: true, dataTransfer, clientX, clientY }));
        source.dispatchEvent(new DragEvent('dragend',   { bubbles: true, cancelable: true, dataTransfer }));
      }""",
      mapOf("sourceSelector" to sourceSelector, "targetSelector" to targetSelector),
    )
  }
}
