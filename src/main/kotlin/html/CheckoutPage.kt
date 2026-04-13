package com.mattbobambrose.html

import com.mattbobambrose.data.Catalog
import com.mattbobambrose.model.CartItem
import com.mattbobambrose.model.formatCents
import com.mattbobambrose.model.lineSubtotalDisplay
import io.ktor.http.HttpStatusCode
import kotlinx.html.HtmlBlockTag
import io.ktor.server.application.ApplicationCall
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.p
import kotlinx.html.section
import kotlinx.html.span
import kotlinx.html.textArea

data class CheckoutForm(
  val name: String = "",
  val email: String = "",
  val address: String = "",
  val cardNumber: String = "",
  val cardExpiry: String = "",
  val cardCvc: String = "",
)

suspend fun ApplicationCall.renderCheckout(
  items: List<CartItem>,
  form: CheckoutForm = CheckoutForm(),
  errors: Map<String, String> = emptyMap(),
) {
  val lines = items.mapNotNull { item -> Catalog.findById(item.bookId)?.let { it to item } }
  val totalCents = lines.sumOf { (book, item) -> book.priceCents * item.qty }
  val status = if (errors.isEmpty()) HttpStatusCode.OK else HttpStatusCode.BadRequest
  respondPage("Checkout", status) {
    h1(classes = "text-3xl font-bold mb-6") { +"Checkout" }
    div(classes = "grid grid-cols-1 lg:grid-cols-[1fr_320px] gap-8") {
      form(action = "/checkout", method = FormMethod.post, classes = "bg-white rounded-xl border border-slate-200 p-6 space-y-4") {
        attributes["data-testid"] = "checkout-form"
        textField("name", "Full name", form.name, errors["name"])
        textField("email", "Email", form.email, errors["email"], type = InputType.email)
        div {
          label(classes = "block text-sm font-medium text-slate-700") {
            attributes["htmlFor"] = "address"
            +"Shipping address"
          }
          textArea(classes = "mt-1 w-full rounded border border-slate-300 px-3 py-2") {
            attributes["id"] = "address"
            attributes["name"] = "address"
            attributes["rows"] = "3"
            attributes["data-testid"] = "input-address"
            +form.address
          }
          fieldError(errors["address"])
        }
        h2(classes = "text-lg font-semibold pt-4") { +"Payment" }
        textField("cardNumber", "Card number", form.cardNumber, errors["cardNumber"])
        div(classes = "grid grid-cols-2 gap-3") {
          textField("cardExpiry", "Expiry (MM/YY)", form.cardExpiry, errors["cardExpiry"])
          textField("cardCvc", "CVC", form.cardCvc, errors["cardCvc"])
        }
        button(type = ButtonType.submit, classes = "mt-4 rounded bg-indigo-600 text-white font-semibold px-5 py-3 hover:bg-indigo-700") {
          attributes["data-testid"] = "place-order"
          +"Place order"
        }
      }
      section(classes = "bg-white rounded-xl border border-slate-200 p-6") {
        attributes["data-testid"] = "checkout-summary"
        h2(classes = "text-lg font-semibold mb-3") { +"Order summary" }
        orderLineRows(lines)
        div(classes = "border-t border-slate-200 mt-3 pt-3 flex justify-between font-semibold") {
          span { +"Total" }
          span {
            attributes["data-testid"] = "checkout-total"
            +formatCents(totalCents)
          }
        }
      }
    }
  }
}

suspend fun ApplicationCall.renderCheckoutConfirmation(
  orderId: String,
  items: List<CartItem>,
  totalCents: Int,
) {
  respondPage("Order placed") {
    section(classes = "bg-white rounded-xl border border-green-200 p-8 text-center") {
      attributes["data-testid"] = "confirmation"
      div(classes = "text-5xl mb-4") { +"✅" }
      h1(classes = "text-2xl font-bold text-green-800") { +"Thank you for your order!" }
      p(classes = "mt-2 text-slate-700") {
        +"Your confirmation number is "
        span(classes = "font-mono font-semibold") {
          attributes["data-testid"] = "confirmation-id"
          +orderId
        }
        +"."
      }
      div(classes = "mt-6 mx-auto max-w-sm text-left text-sm") {
        val lines = items.mapNotNull { item -> Catalog.findById(item.bookId)?.let { it to item } }
        orderLineRows(lines)
        div(classes = "border-t border-slate-200 mt-2 pt-2 flex justify-between font-semibold") {
          span { +"Total" }
          span { +formatCents(totalCents) }
        }
      }
    }
  }
}

private fun HtmlBlockTag.orderLineRows(lines: List<Pair<com.mattbobambrose.model.Book, CartItem>>) {
  lines.forEach { (book, item) ->
    div(classes = "flex justify-between py-1") {
      span { +"${book.title} × ${item.qty}" }
      span(classes = "text-slate-700") { +lineSubtotalDisplay(book, item.qty) }
    }
  }
}
