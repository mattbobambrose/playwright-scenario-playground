package com.mattbobambrose.routing

import com.mattbobambrose.data.Users
import com.mattbobambrose.html.renderLogin
import com.mattbobambrose.session.UserSession
import io.ktor.server.application.Application
import io.ktor.server.request.receiveParameters
import com.mattbobambrose.routing.respondSeeOther
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

fun Application.configureAuthRouting() {
  routing {
    get("/login") {
      val next = call.request.queryParameters["next"] ?: "/"
      call.renderLogin(next = next)
    }
    post("/login") {
      val params = call.receiveParameters()
      val username = params["username"].orEmpty().trim()
      val password = params["password"].orEmpty()
      val next = params["next"].orEmpty().ifBlank { "/" }.let { if (it.startsWith("/")) it else "/" }
      val user = Users.authenticate(username, password)
      if (user == null) {
        call.renderLogin(next = next, username = username, error = "Invalid username or password.")
        return@post
      }
      call.sessions.set(UserSession(user.username))
      call.respondSeeOther(next)
    }
    post("/logout") {
      call.sessions.clear<UserSession>()
      call.respondSeeOther("/")
    }
  }
}
