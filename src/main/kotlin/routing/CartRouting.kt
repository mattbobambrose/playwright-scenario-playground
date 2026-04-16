package com.mattbobambrose.routing

import com.mattbobambrose.data.Catalog
import com.mattbobambrose.data.Stores
import com.mattbobambrose.html.renderCart
import com.mattbobambrose.session.cartKey
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import com.mattbobambrose.routing.respondSeeOther
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureCartRouting() {
  routing {
    get("/cart") {
      val items = Stores.cartFor(call.cartKey()).snapshot()
      call.renderCart(items)
    }
    post("/cart/add") {
      val params = call.receiveParameters()
      val bookId = params["bookId"]?.toIntOrNull()
      val qty = params["qty"]?.toIntOrNull()?.coerceIn(1, 99) ?: 1
      if (bookId == null || Catalog.findById(bookId) == null) {
        call.respond(HttpStatusCode.BadRequest, "Unknown book")
        return@post
      }
      Stores.cartFor(call.cartKey()).add(bookId, qty)
      call.respondSeeOther("/cart")
    }
    post("/cart/{id}/qty") {
      val bookId = call.parameters["id"]?.toIntOrNull()
      val qty = call.receiveParameters()["qty"]?.toIntOrNull()
      if (bookId == null || qty == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
      }
      Stores.cartFor(call.cartKey()).setQty(bookId, qty.coerceIn(0, 99))
      call.respondSeeOther("/cart")
    }
    post("/cart/{id}/remove") {
      val bookId = call.parameters["id"]?.toIntOrNull()
      if (bookId == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@post
      }
      Stores.cartFor(call.cartKey()).remove(bookId)
      call.respondSeeOther("/cart")
    }
  }
}
