package com.mattbobambrose.routing

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend fun ApplicationCall.respondSeeOther(location: String) {
  response.headers.append(HttpHeaders.Location, location)
  respond(HttpStatusCode.SeeOther)
}
