package com.mattbobambrose.routing

import com.mattbobambrose.data.Catalog
import com.mattbobambrose.data.Stores
import com.mattbobambrose.html.renderShelves
import com.mattbobambrose.model.Shelf
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

fun Application.configureShelfRouting() {
  routing {
    get("/shelves") {
      val session = call.requireUser() ?: return@get
      val data = Shelf.entries.associateWith { Stores.shelvesFor(session.username).booksOn(it) }
      call.renderShelves(data)
    }
    post("/shelves/add") {
      val session = call.requireUser() ?: return@post
      val params = call.receiveParameters()
      val bookId = params["bookId"]?.toIntOrNull()
      val shelf = Shelf.fromParam(params["shelf"]) ?: Shelf.WANT
      if (bookId == null || Catalog.findById(bookId) == null) {
        call.respond(HttpStatusCode.BadRequest, "Unknown book")
        return@post
      }
      Stores.shelvesFor(session.username).addIfAbsent(bookId, shelf)
      call.respondSeeOther("/books/$bookId?added=${shelf.display}")
    }
    post("/shelves/move") {
      val session = call.requireUser() ?: return@post
      val body = call.receive<MoveRequest>()
      val from = Shelf.fromParam(body.fromShelf)
      val to = Shelf.fromParam(body.toShelf)
      if (from == null || to == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid shelf")
        return@post
      }
      Stores.shelvesFor(session.username).move(body.bookId, from, to, body.beforeBookId)
      call.respond(HttpStatusCode.NoContent)
    }
    post("/shelves/reorder") {
      val session = call.requireUser() ?: return@post
      val body = call.receive<ReorderRequest>()
      val shelf = Shelf.fromParam(body.shelf)
      if (shelf == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid shelf")
        return@post
      }
      Stores.shelvesFor(session.username).reorder(shelf, body.bookId, body.beforeBookId)
      call.respond(HttpStatusCode.NoContent)
    }
  }
}

@Serializable
data class MoveRequest(
  val bookId: Int,
  val fromShelf: String,
  val toShelf: String,
  val beforeBookId: Int? = null,
)

@Serializable
data class ReorderRequest(
  val shelf: String,
  val bookId: Int,
  val beforeBookId: Int? = null,
)
