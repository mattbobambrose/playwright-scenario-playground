package com.mattbobambrose.examples.scenarios

import com.mattbobambrose.examples.BasePageTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith

class CatalogBrowseTest : BasePageTest("/catalog") {
  init {
    "Catalog grid renders the first page of books" {
      page.locator("h1").textContent() shouldBe "Catalog"
      page.locator("[data-testid=catalog-grid]").isVisible shouldBe true
      page.locator("[data-testid=catalog-grid] [data-testid^=book-card-]").count() shouldBe 6
      page.locator("[data-testid=catalog-grid] [data-testid^=book-card-]").first()
        .getAttribute("data-testid") shouldBe "book-card-5"
    }

    "Genre filter narrows the result set" {
      page.selectOption("[data-testid=filter-genre]", "scifi")
      page.locator("[data-testid=filter-apply]").click()
      page.waitForURL("**/catalog**")

      page.url() shouldContain "genre=scifi"
      val cards = page.locator("[data-testid=catalog-grid] [data-testid^=book-card-]")
      cards.count() shouldBe 4
      page.locator("[data-testid=book-card-2]").isVisible shouldBe true
      page.locator("[data-testid=book-card-8]").isVisible shouldBe true
      page.locator("[data-testid=book-card-13]").isVisible shouldBe true
      page.locator("[data-testid=book-card-18]").isVisible shouldBe true
      page.locator("[data-testid=pagination]").isVisible shouldBe false
    }

    "Search box narrows by title or author" {
      page.locator("[data-testid=filter-q]").fill("Mars")
      page.locator("[data-testid=filter-apply]").click()
      page.waitForURL("**/catalog**")

      page.url() shouldContain "q=Mars"
      page.locator("[data-testid=catalog-grid] [data-testid^=book-card-]").count() shouldBe 1
      page.locator("[data-testid=book-card-2]").isVisible shouldBe true
    }

    "Empty state appears when no books match" {
      page.locator("[data-testid=filter-q]").fill("zzzzzzz")
      page.locator("[data-testid=filter-apply]").click()
      page.waitForURL("**/catalog**")

      page.locator("[data-testid=catalog-grid]").isVisible shouldBe false
      page.locator("[data-testid=catalog-empty]").isVisible shouldBe true
      page.locator("[data-testid=catalog-empty]").textContent() shouldContain "No books match your search."
    }

    "Clear link resets filters" {
      page.locator("[data-testid=filter-q]").fill("Mars")
      page.locator("[data-testid=filter-apply]").click()
      page.waitForURL("**/catalog**")

      page.locator("[data-testid=filter-clear]").click()
      page.waitForURL("**/catalog")

      page.url() shouldEndWith "/catalog"
      page.locator("[data-testid=catalog-grid] [data-testid^=book-card-]").count() shouldBe 6
    }

    "Pagination advances to page 2" {
      page.locator("[data-testid=pagination-next]").isVisible shouldBe true
      page.locator("[data-testid=pagination-next]").click()
      page.waitForURL("**/catalog**")

      page.url() shouldContain "page=2"
      page.locator("[data-testid=pagination]").textContent() shouldContain "Page 2 of 4"
      page.locator("[data-testid=book-card-5]").isVisible shouldBe false
    }

    "Book card link navigates to the detail page" {
      page.locator("[data-testid=book-card-5] a").click()
      page.waitForURL("**/books/5")

      page.url() shouldEndWith "/books/5"
      page.locator("[data-testid=book-title]").textContent() shouldBe "A Quiet Revolution"
      page.locator("[data-testid=book-price]").textContent() shouldContain "\$16.99"
    }
  }
}
