package com.mattbobambrose.html

import com.mattbobambrose.data.Catalog
import com.mattbobambrose.model.Book
import com.mattbobambrose.model.Genre
import com.mattbobambrose.model.SortOrder
import io.ktor.http.encodeURLParameter
import io.ktor.server.application.ApplicationCall
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.HtmlBlockTag
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.span

data class CatalogQuery(
  val q: String = "",
  val genre: Genre? = null,
  val sort: SortOrder = SortOrder.default,
  val page: Int = 1,
) {
  /** Returns a query string suffix including the leading `?`, or an empty string. */
  fun toQueryString(overrides: Map<String, String?> = emptyMap()): String {
    val params = buildMap<String, String?> {
      put("q", q.ifBlank { null })
      put("genre", genre?.name?.lowercase())
      put("sort", sort.takeIf { it != SortOrder.default }?.param)
      put("page", page.takeIf { it > 1 }?.toString())
      overrides.forEach { (k, v) -> put(k, v) }
    }
    val encoded = params.filterValues { !it.isNullOrBlank() }
      .map { (k, v) -> "$k=${v!!.encodeURLParameter()}" }
    return if (encoded.isEmpty()) "" else "?${encoded.joinToString("&")}"
  }
}

suspend fun ApplicationCall.renderCatalog(query: CatalogQuery) {
  val pageSize = 6
  val filtered = Catalog.books.asSequence()
    .filter { query.q.isBlank() || it.title.contains(query.q, ignoreCase = true) || it.author.contains(query.q, ignoreCase = true) }
    .filter { query.genre == null || it.genre == query.genre }
    .toList()
  val sorted = when (query.sort) {
    SortOrder.PRICE_ASC -> filtered.sortedBy { it.priceCents }
    SortOrder.PRICE_DESC -> filtered.sortedByDescending { it.priceCents }
    SortOrder.RATING -> filtered.sortedByDescending { it.rating }
    SortOrder.TITLE -> filtered.sortedBy { it.title.lowercase() }
  }
  val totalPages = ((sorted.size + pageSize - 1) / pageSize).coerceAtLeast(1)
  val page = query.page.coerceIn(1, totalPages)
  val pageItems = sorted.drop((page - 1) * pageSize).take(pageSize)

  respondPage("Catalog") {
    h1(classes = "text-3xl font-bold mb-6") { +"Catalog" }
    renderFilters(query)
    if (pageItems.isEmpty()) {
      div(classes = "mt-8 rounded border border-dashed border-slate-300 p-8 text-center text-slate-500") {
        attributes["data-testid"] = "catalog-empty"
        +"No books match your search."
      }
    } else {
      div(classes = "mt-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4") {
        attributes["data-testid"] = "catalog-grid"
        pageItems.forEach { catalogCard(it) }
      }
      renderPagination(query, page, totalPages)
    }
  }
}

private fun HtmlBlockTag.renderFilters(query: CatalogQuery) {
  form(action = "/catalog", method = FormMethod.get, classes = "grid grid-cols-1 md:grid-cols-4 gap-3 items-end") {
    attributes["data-testid"] = "catalog-filters"
    div {
      label(classes = "block text-sm font-medium text-slate-700") {
        attributes["htmlFor"] = "q"
        +"Search"
      }
      input(type = InputType.text, name = "q", classes = "mt-1 w-full rounded border border-slate-300 px-3 py-2") {
        attributes["id"] = "q"
        attributes["data-testid"] = "filter-q"
        attributes["placeholder"] = "Title or author"
        value = query.q
      }
    }
    div {
      label(classes = "block text-sm font-medium text-slate-700") {
        attributes["htmlFor"] = "genre"
        +"Genre"
      }
      select(classes = "mt-1 w-full rounded border border-slate-300 px-3 py-2") {
        attributes["id"] = "genre"
        attributes["name"] = "genre"
        attributes["data-testid"] = "filter-genre"
        option {
          attributes["value"] = ""
          if (query.genre == null) attributes["selected"] = "selected"
          +"All genres"
        }
        Genre.entries.forEach { g ->
          option {
            attributes["value"] = g.name.lowercase()
            if (query.genre == g) attributes["selected"] = "selected"
            +g.display
          }
        }
      }
    }
    div {
      label(classes = "block text-sm font-medium text-slate-700") {
        attributes["htmlFor"] = "sort"
        +"Sort"
      }
      select(classes = "mt-1 w-full rounded border border-slate-300 px-3 py-2") {
        attributes["id"] = "sort"
        attributes["name"] = "sort"
        attributes["data-testid"] = "filter-sort"
        SortOrder.entries.forEach { sort ->
          option {
            attributes["value"] = sort.param
            if (query.sort == sort) attributes["selected"] = "selected"
            +sort.display
          }
        }
      }
    }
    div(classes = "flex gap-2") {
      button(type = ButtonType.submit, classes = "rounded bg-indigo-600 text-white font-semibold px-4 py-2 hover:bg-indigo-700") {
        attributes["data-testid"] = "filter-apply"
        +"Apply"
      }
      a(href = "/catalog", classes = "rounded border border-slate-300 px-4 py-2 text-slate-700 hover:border-slate-500") {
        attributes["data-testid"] = "filter-clear"
        +"Clear"
      }
    }
  }
}

private fun HtmlBlockTag.catalogCard(book: Book) {
  div(classes = "rounded-xl bg-white border border-slate-200 p-4 flex flex-col") {
    attributes["data-testid"] = "book-card-${book.id}"
    a(href = "/books/${book.id}", classes = "block") {
      div(classes = "text-5xl mb-3") { +book.coverEmoji }
      h2(classes = "font-semibold text-slate-900 hover:text-indigo-700") { +book.title }
    }
    p(classes = "text-sm text-slate-500 mt-1") { +book.author }
    p(classes = "text-xs uppercase tracking-wide text-slate-400 mt-1") { +book.genre.display }
    div(classes = "mt-4 flex items-center justify-between text-sm") {
      span(classes = "text-indigo-700 font-semibold") {
        attributes["data-testid"] = "book-price-${book.id}"
        +book.priceDisplay
      }
      span(classes = "text-amber-600") { +"★ ${"%.1f".format(book.rating)}" }
    }
  }
}

private fun HtmlBlockTag.renderPagination(query: CatalogQuery, page: Int, totalPages: Int) {
  if (totalPages <= 1) return
  div(classes = "mt-8 flex items-center justify-between") {
    attributes["data-testid"] = "pagination"
    span(classes = "text-sm text-slate-500") { +"Page $page of $totalPages" }
    div(classes = "flex gap-2") {
      val prevEnabled = page > 1
      val nextEnabled = page < totalPages
      paginationLink(query, page - 1, "Previous", "pagination-prev", prevEnabled)
      paginationLink(query, page + 1, "Next", "pagination-next", nextEnabled)
    }
  }
}

private fun HtmlBlockTag.paginationLink(query: CatalogQuery, targetPage: Int, label: String, testId: String, enabled: Boolean) {
  if (enabled) {
    a(href = "/catalog${query.toQueryString(mapOf("page" to targetPage.toString()))}",
      classes = "rounded border border-slate-300 px-3 py-1.5 text-sm text-slate-700 hover:border-slate-500") {
      attributes["data-testid"] = testId
      +label
    }
  } else {
    span(classes = "rounded border border-slate-200 px-3 py-1.5 text-sm text-slate-400 cursor-not-allowed") {
      attributes["data-testid"] = testId
      attributes["aria-disabled"] = "true"
      +label
    }
  }
}
