package com.mattbobambrose.routing

import com.mattbobambrose.data.Stores
import com.mattbobambrose.html.renderOrderHistory
import io.ktor.server.application.Application
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureOrderRouting() {
  routing {
    get("/orders") {
      val session = call.requireUser() ?: return@get
      call.renderOrderHistory(Stores.ordersFor(session.username))
    }
  }
}
