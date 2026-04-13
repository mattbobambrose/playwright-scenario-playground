package com.mattbobambrose.routing

import com.mattbobambrose.data.Catalog
import com.mattbobambrose.data.Stores
import com.mattbobambrose.html.CheckoutForm
import com.mattbobambrose.html.renderCheckout
import com.mattbobambrose.html.renderCheckoutConfirmation
import com.mattbobambrose.model.Order
import com.mattbobambrose.session.UserSession
import com.mattbobambrose.session.cartKey
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import com.mattbobambrose.routing.respondSeeOther
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import java.time.Instant

fun Application.configureCheckoutRouting() {
  routing {
    get("/checkout") {
      val items = Stores.cartFor(call.cartKey()).snapshot()
      if (items.isEmpty()) {
        call.respondSeeOther("/cart")
        return@get
      }
      call.renderCheckout(items)
    }
    post("/checkout") {
      val params = call.receiveParameters()
      val form = CheckoutForm(
        name = params["name"].orEmpty().trim(),
        email = params["email"].orEmpty().trim(),
        address = params["address"].orEmpty().trim(),
        cardNumber = params["cardNumber"].orEmpty().trim(),
        cardExpiry = params["cardExpiry"].orEmpty().trim(),
        cardCvc = params["cardCvc"].orEmpty().trim(),
      )
      val errors = validateCheckout(form)
      val items = Stores.cartFor(call.cartKey()).snapshot()
      if (items.isEmpty()) {
        call.respondSeeOther("/cart")
        return@post
      }
      if (errors.isNotEmpty()) {
        call.renderCheckout(items, form, errors)
        return@post
      }
      val totalCents = items.sumOf { item ->
        (Catalog.findById(item.bookId)?.priceCents ?: 0) * item.qty
      }
      val username = call.sessions.get<UserSession>()?.username ?: "guest"
      val order = Order(
        id = Stores.nextOrderId(),
        username = username,
        items = items,
        totalCents = totalCents,
        shippingName = form.name,
        shippingAddress = form.address,
        placedAt = Instant.now(),
      )
      Stores.addOrder(order)
      Stores.cartFor(call.cartKey()).clear()
      call.respondSeeOther("/checkout/confirmation/${order.id}")
    }
    get("/checkout/confirmation/{orderId}") {
      val order = call.parameters["orderId"]?.let { Stores.findOrder(it) }
      if (order == null) {
        call.respond(HttpStatusCode.NotFound, "Order not found")
        return@get
      }
      call.renderCheckoutConfirmation(order.id, order.items, order.totalCents)
    }
  }
}

private val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
private val cardDigitsRegex = Regex("^[0-9]{13,19}$")
private val expiryRegex = Regex("^(0[1-9]|1[0-2])/\\d{2}$")
private val cvcRegex = Regex("^[0-9]{3,4}$")

private fun validateCheckout(form: CheckoutForm): Map<String, String> = buildMap {
  if (form.name.isBlank()) put("name", "Please enter your full name.")
  if (form.email.isBlank()) put("email", "Email is required.")
  else if (!emailRegex.matches(form.email)) put("email", "That doesn't look like a valid email.")
  if (form.address.isBlank()) put("address", "Shipping address is required.")
  val cardDigits = form.cardNumber.replace(" ", "").replace("-", "")
  if (cardDigits.isBlank()) put("cardNumber", "Card number is required.")
  else if (!cardDigitsRegex.matches(cardDigits)) put("cardNumber", "Card number must be 13–19 digits.")
  if (form.cardExpiry.isBlank()) put("cardExpiry", "Expiry is required.")
  else if (!expiryRegex.matches(form.cardExpiry)) put("cardExpiry", "Use MM/YY format.")
  if (form.cardCvc.isBlank()) put("cardCvc", "CVC is required.")
  else if (!cvcRegex.matches(form.cardCvc)) put("cardCvc", "CVC must be 3 or 4 digits.")
}
