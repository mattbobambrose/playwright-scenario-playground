package com.mattbobambrose.html

import com.mattbobambrose.model.Book
import com.mattbobambrose.model.Shelf
import io.ktor.server.application.ApplicationCall
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.hiddenInput
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.section
import kotlinx.html.select
import kotlinx.html.span

suspend fun ApplicationCall.renderBookDetail(book: Book, showShelfPicker: Boolean, flashMessage: String?) {
  respondPage(book.title) {
    a(href = "/catalog", classes = "text-sm text-indigo-700 hover:underline") { +"← Back to catalog" }
    if (flashMessage != null) {
      div(classes = "mt-4 rounded border border-green-200 bg-green-50 px-4 py-3 text-green-800") {
        attributes["data-testid"] = "flash-success"
        +flashMessage
      }
    }
    section(classes = "mt-4 grid grid-cols-1 md:grid-cols-[200px_1fr] gap-8 bg-white rounded-xl border border-slate-200 p-6") {
      attributes["data-testid"] = "book-detail-${book.id}"
      div(classes = "text-9xl text-center") { +book.coverEmoji }
      div {
        h1(classes = "text-3xl font-bold text-slate-900") {
          attributes["data-testid"] = "book-title"
          +book.title
        }
        p(classes = "text-slate-600 mt-1") { +"by ${book.author}" }
        div(classes = "mt-2 flex gap-3 text-sm") {
          span(classes = "rounded-full bg-slate-100 px-3 py-1 text-slate-700") { +book.genre.display }
          span(classes = "text-amber-600") { +"★ ${"%.1f".format(book.rating)}" }
          span(classes = "text-indigo-700 font-semibold text-lg") {
            attributes["data-testid"] = "book-price"
            +book.priceDisplay
          }
        }
        p(classes = "mt-4 text-slate-700 leading-relaxed") { +book.blurb }
        div(classes = "mt-6 flex flex-wrap gap-3") {
          form(action = "/cart/add", method = FormMethod.post) {
            hiddenInput(name = "bookId") { value = book.id.toString() }
            button(type = ButtonType.submit, classes = "rounded bg-indigo-600 text-white font-semibold px-5 py-2 hover:bg-indigo-700") {
              attributes["data-testid"] = "add-to-cart"
              +"Add to Cart"
            }
          }
          if (showShelfPicker) {
            form(action = "/shelves/add", method = FormMethod.post, classes = "flex gap-2 items-center") {
              hiddenInput(name = "bookId") { value = book.id.toString() }
              select(classes = "rounded border border-slate-300 px-3 py-2") {
                attributes["name"] = "shelf"
                attributes["data-testid"] = "shelf-select"
                Shelf.entries.forEach { shelf ->
                  option {
                    attributes["value"] = shelf.name.lowercase()
                    +shelf.display
                  }
                }
              }
              button(type = ButtonType.submit, classes = "rounded border border-indigo-300 text-indigo-700 font-semibold px-4 py-2 hover:bg-indigo-50") {
                attributes["data-testid"] = "add-to-shelf"
                +"Add to Shelf"
              }
            }
          } else {
            a(href = "/login?next=/books/${book.id}", classes = "text-sm text-indigo-700 hover:underline self-center") {
              attributes["data-testid"] = "login-to-shelf"
              +"Log in to add to your shelves →"
            }
          }
        }
      }
    }
  }
}
