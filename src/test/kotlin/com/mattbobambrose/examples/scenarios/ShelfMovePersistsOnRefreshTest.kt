package com.mattbobambrose.examples.scenarios

import com.mattbobambrose.examples.BasePageTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class ShelfMovePersistsOnRefreshTest : BasePageTest("/login") {
  init {
    "Drag a book from Want to Read to Reading and refresh immediately" {
      page.locator("[data-testid=input-username]").fill("alice")
      page.locator("[data-testid=input-password]").fill("wonderland")
      page.locator("[data-testid=login-submit]").click()
      page.locator("[data-testid=nav-user]").waitFor()

      page.navigate("/books/1")
      page.selectOption("[data-testid=shelf-select]", "want")
      page.locator("[data-testid=add-to-shelf]").click()
      page.waitForURL("**/books/1?added=**")

      page.navigate("/shelves")
      page.locator("[data-testid=shelf-count-want]").textContent() shouldBe "1"
      page.locator("[data-testid=shelf-want] [data-testid=shelf-book-1]").count() shouldBe 1
      page.locator("[data-testid=shelf-count-reading]").textContent() shouldBe "0"

      dispatchHtml5Drag(
        sourceSelector = "[data-testid=shelf-book-1]",
        targetSelector = "[data-testid=shelf-reading]",
      )
      page.locator("[data-testid=shelf-reading] [data-testid=shelf-book-1]").waitFor()
      page.reload()

      page.locator("[data-testid=shelf-count-reading]").textContent() shouldBe "1"
      page.locator("[data-testid=shelf-reading] [data-testid=shelf-book-1]").count() shouldBe 1
      page.locator("[data-testid=shelf-count-want]").textContent() shouldBe "0"
      page.locator("[data-testid=shelf-empty-want]").textContent() shouldContain "Drop a book here"
    }
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
