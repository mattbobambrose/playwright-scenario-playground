package com.mattbobambrose.html

import com.mattbobambrose.data.Catalog
import com.mattbobambrose.model.CartItem
import com.mattbobambrose.model.formatCents
import com.mattbobambrose.model.lineSubtotalDisplay
import io.ktor.server.application.ApplicationCall
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.hiddenInput
import kotlinx.html.input
import kotlinx.html.p
import kotlinx.html.section
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

suspend fun ApplicationCall.renderCart(items: List<CartItem>) {
  val withBook = items.mapNotNull { item -> Catalog.findById(item.bookId)?.let { it to item } }
  val totalCents = withBook.sumOf { (book, item) -> book.priceCents * item.qty }
  respondPage("Cart") {
    h1(classes = "text-3xl font-bold mb-6") { +"Your Cart" }
    if (withBook.isEmpty()) {
      section(classes = "rounded border border-dashed border-slate-300 p-10 text-center") {
        attributes["data-testid"] = "cart-empty"
        p(classes = "text-slate-500") { +"Your cart is empty." }
        a(href = "/catalog", classes = "mt-3 inline-block text-indigo-700 hover:underline") { +"Browse the catalog →" }
      }
    } else {
      section(classes = "rounded-xl bg-white border border-slate-200 overflow-hidden") {
        table(classes = "w-full text-sm") {
          attributes["data-testid"] = "cart-table"
          thead(classes = "bg-slate-50 text-left text-slate-600") {
            tr {
              th(classes = "px-4 py-3") { +"Book" }
              th(classes = "px-4 py-3") { +"Price" }
              th(classes = "px-4 py-3") { +"Qty" }
              th(classes = "px-4 py-3") { +"Subtotal" }
              th(classes = "px-4 py-3") { +"" }
            }
          }
          tbody {
            withBook.forEach { (book, item) ->
              tr(classes = "border-t border-slate-100") {
                attributes["data-testid"] = "cart-row-${book.id}"
                td(classes = "px-4 py-3") {
                  a(href = "/books/${book.id}", classes = "text-slate-900 hover:text-indigo-700 font-medium") {
                    +"${book.coverEmoji} ${book.title}"
                  }
                }
                td(classes = "px-4 py-3 text-slate-700") { +book.priceDisplay }
                td(classes = "px-4 py-3") {
                  form(action = "/cart/${book.id}/qty", method = FormMethod.post, classes = "flex gap-2 items-center") {
                    input(type = InputType.number, name = "qty", classes = "w-20 rounded border border-slate-300 px-2 py-1") {
                      value = item.qty.toString()
                      attributes["min"] = "1"
                      attributes["max"] = "99"
                      attributes["data-testid"] = "cart-qty-${book.id}"
                    }
                    button(type = ButtonType.submit, classes = "text-xs text-indigo-700 hover:underline") {
                      attributes["data-testid"] = "cart-update-${book.id}"
                      +"Update"
                    }
                  }
                }
                td(classes = "px-4 py-3 text-slate-900 font-medium") {
                  attributes["data-testid"] = "cart-subtotal-${book.id}"
                  +lineSubtotalDisplay(book, item.qty)
                }
                td(classes = "px-4 py-3") {
                  form(action = "/cart/${book.id}/remove", method = FormMethod.post) {
                    hiddenInput(name = "_method") { value = "delete" }
                    button(type = ButtonType.submit, classes = "text-sm text-red-600 hover:underline") {
                      attributes["data-testid"] = "cart-remove-${book.id}"
                      +"Remove"
                    }
                  }
                }
              }
            }
          }
        }
      }
      div(classes = "mt-6 flex items-center justify-between") {
        span(classes = "text-lg font-semibold") {
          +"Total: "
          span {
            attributes["data-testid"] = "cart-total"
            +formatCents(totalCents)
          }
        }
        a(href = "/checkout", classes = "rounded bg-indigo-600 px-5 py-3 text-white font-semibold hover:bg-indigo-700") {
          attributes["data-testid"] = "checkout-button"
          +"Check out"
        }
      }
    }
  }
}
