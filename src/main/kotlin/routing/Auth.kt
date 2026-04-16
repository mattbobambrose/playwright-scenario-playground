package com.mattbobambrose.routing

import com.mattbobambrose.session.UserSession
import io.ktor.http.encodeURLQueryComponent
import io.ktor.server.application.ApplicationCall
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

suspend fun ApplicationCall.requireUser(): UserSession? {
  val session = sessions.get<UserSession>()
  if (session == null) {
    respondSeeOther("/login?next=${request.local.uri.encodeURLQueryComponent()}")
    return null
  }
  return session
}
