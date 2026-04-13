package com.mattbobambrose.html

import com.mattbobambrose.data.Catalog
import com.mattbobambrose.model.Book
import io.ktor.server.application.ApplicationCall
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.p
import kotlinx.html.section
import kotlinx.html.span

suspend fun ApplicationCall.renderHome() {
  val featured = Catalog.books.sortedByDescending { it.rating }.take(4)
  respondPage("Home") {
    section(classes = "bg-gradient-to-br from-indigo-100 via-white to-amber-100 rounded-2xl p-10 mb-10") {
      attributes["data-testid"] = "hero"
      h1(classes = "text-4xl md:text-5xl font-extrabold tracking-tight text-slate-900") {
        +"Your next favorite book is on the shelf."
      }
      p(classes = "mt-4 text-lg text-slate-700 max-w-2xl") {
        +"Browse twenty hand-picked titles across fiction, nonfiction, mystery, sci-fi, and biography. Build a cart, check out, and track what you're reading across three personal shelves."
      }
      div(classes = "mt-6 flex gap-3") {
        a(href = "/catalog", classes = "rounded-lg bg-indigo-600 px-5 py-3 text-white font-semibold shadow hover:bg-indigo-700") {
          attributes["data-testid"] = "cta-browse"
          +"Browse the catalog"
        }
        a(href = "/login", classes = "rounded-lg bg-white px-5 py-3 text-indigo-700 font-semibold border border-indigo-200 hover:border-indigo-400") {
          attributes["data-testid"] = "cta-login"
          +"Log in"
        }
      }
    }

    section {
      h2(classes = "text-2xl font-bold text-slate-900 mb-4") { +"Top-rated this week" }
      div(classes = "grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4") {
        attributes["data-testid"] = "featured-grid"
        featured.forEach { featuredCard(it) }
      }
    }
  }
}

private fun kotlinx.html.HtmlBlockTag.featuredCard(book: Book) {
  a(href = "/books/${book.id}", classes = "block rounded-xl bg-white border border-slate-200 p-4 hover:shadow-md hover:border-indigo-300 transition") {
    attributes["data-testid"] = "featured-${book.id}"
    div(classes = "text-5xl mb-3") { +book.coverEmoji }
    div(classes = "font-semibold text-slate-900 line-clamp-2") { +book.title }
    div(classes = "text-sm text-slate-500") { +book.author }
    div(classes = "mt-2 flex items-center justify-between text-sm") {
      span(classes = "text-indigo-700 font-semibold") { +book.priceDisplay }
      span(classes = "text-amber-600") { +"★ ${"%.1f".format(book.rating)}" }
    }
  }
}
