package com.mattbobambrose.routing

import com.mattbobambrose.data.Catalog
import com.mattbobambrose.html.CatalogQuery
import com.mattbobambrose.html.renderBookDetail
import com.mattbobambrose.html.renderCatalog
import com.mattbobambrose.model.Genre
import com.mattbobambrose.model.SortOrder
import com.mattbobambrose.session.UserSession
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

fun Application.configureCatalogRouting() {
  routing {
    get("/catalog") {
      val q = call.request.queryParameters["q"].orEmpty()
      val genre = Genre.fromParam(call.request.queryParameters["genre"])
      val sort = SortOrder.fromParam(call.request.queryParameters["sort"])
      val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
      call.renderCatalog(CatalogQuery(q, genre, sort, page))
    }
    get("/books/{id}") {
      val id = call.parameters["id"]?.toIntOrNull()
      val book = id?.let { Catalog.findById(it) }
      if (book == null) {
        call.respond(HttpStatusCode.NotFound, "Book not found")
        return@get
      }
      val loggedIn = call.sessions.get<UserSession>() != null
      val flash = call.request.queryParameters["added"]?.let { "$it added." }
      call.renderBookDetail(book, showShelfPicker = loggedIn, flashMessage = flash)
    }
  }
}
