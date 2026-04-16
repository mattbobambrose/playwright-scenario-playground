package com.mattbobambrose.session

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kotlinx.serialization.Serializable

fun Application.configureSessions() {
  install(Sessions) {
    cookie<UserSession>("USER_SESSION") {
      cookie.path = "/"
      cookie.httpOnly = true
      cookie.maxAgeInSeconds = 60 * 60 * 24 * 7
    }
    cookie<GuestSession>("GUEST_SESSION") {
      cookie.path = "/"
      cookie.httpOnly = true
      cookie.maxAgeInSeconds = 60 * 60 * 24 * 30
    }
  }
}

@Serializable
data class GuestSession(val id: String)
