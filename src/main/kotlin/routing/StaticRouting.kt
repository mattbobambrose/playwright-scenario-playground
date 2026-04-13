package com.mattbobambrose.routing

import com.mattbobambrose.html.renderAbout
import com.mattbobambrose.html.renderHome
import io.ktor.server.application.Application
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureStaticRouting() {
  routing {
    get("/") { call.renderHome() }
    get("/about") { call.renderAbout() }
  }
}
