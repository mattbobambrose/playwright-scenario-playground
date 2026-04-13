package com.mattbobambrose.session

import io.ktor.server.application.ApplicationCall
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import java.util.UUID

fun ApplicationCall.cartKeyOrNull(): String? {
  sessions.get<UserSession>()?.let { return "user:${it.username}" }
  return sessions.get<GuestSession>()?.let { "guest:${it.id}" }
}

fun ApplicationCall.cartKey(): String {
  cartKeyOrNull()?.let { return it }
  val guest = GuestSession(UUID.randomUUID().toString())
  sessions.set(guest)
  return "guest:${guest.id}"
}
