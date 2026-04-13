package com.mattbobambrose

import io.ktor.server.application.*

fun main(args: Array<String>) {
  io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
  configureMonitoring()
  configureTemplating()
  configureRouting()
}
