package com.mattbobambrose

import com.mattbobambrose.routing.configureAuthRouting
import com.mattbobambrose.routing.configureCartRouting
import com.mattbobambrose.routing.configureCatalogRouting
import com.mattbobambrose.routing.configureCheckoutRouting
import com.mattbobambrose.routing.configureOrderRouting
import com.mattbobambrose.routing.configureShelfRouting
import com.mattbobambrose.routing.configureStaticRouting
import com.mattbobambrose.session.configureSessions
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun main(args: Array<String>) {
  io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
  install(ContentNegotiation) { json() }
  configureSessions()
  configureMonitoring()
  configureStaticRouting()
  configureCatalogRouting()
  configureCartRouting()
  configureCheckoutRouting()
  configureAuthRouting()
  configureShelfRouting()
  configureOrderRouting()
}
