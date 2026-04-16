package com.mattbobambrose.data

import com.mattbobambrose.model.Book
import com.mattbobambrose.model.Genre

object Catalog {
  val books: List<Book> = listOf(
    Book(1, "The Silent Ring", "Eleanor Vance", Genre.FICTION, 1499, 4.6, "📕",
      "A haunting portrait of a family torn apart by a single unspoken promise."),
    Book(2, "Echoes of Mars", "Henrik Voss", Genre.SCIFI, 1899, 4.4, "🔭",
      "Colonists uncover signals from a long-dead civilization buried under the Martian ice."),
    Book(3, "The Cartographer's Daughter", "Mira Okonkwo", Genre.FICTION, 1299, 4.2, "🗺️",
      "A young woman retraces her father's impossible maps to find the village he kept hidden."),
    Book(4, "Deep Water", "James Crane", Genre.MYSTERY, 1399, 4.1, "🌊",
      "A small-town sheriff investigates the body found where the lake has no bottom."),
    Book(5, "A Quiet Revolution", "Amelia Hart", Genre.NONFICTION, 1699, 4.5, "📗",
      "How a generation of librarians changed American cities one branch at a time."),
    Book(6, "Hollow Lantern", "Rafael Duarte", Genre.MYSTERY, 1599, 4.3, "🕵️",
      "A private detective with a failing memory tries to solve the case that made him."),
    Book(7, "Salt and Iron", "Kestrel Hu", Genre.FICTION, 1399, 4.0, "📘",
      "Two sisters return to the coast their mother fled, and to the question she never answered."),
    Book(8, "The Lunar Protocol", "Priya Narayan", Genre.SCIFI, 1799, 4.7, "🌙",
      "Orbital station crew must decide whose rules to follow when Earth goes dark."),
    Book(9, "Field Notes on Listening", "Theo Abrams", Genre.NONFICTION, 1599, 4.4, "🎧",
      "A sound recordist's meditation on attention, cities, and the disappearing quiet."),
    Book(10, "Rook & Raven", "Clara Denholm", Genre.MYSTERY, 1299, 3.9, "♟️",
      "A chess prodigy disappears from a locked room; her coach has three days."),
    Book(11, "Beneath the Aurora", "Sigrid Halldor", Genre.FICTION, 1449, 4.2, "🌌",
      "A widowed schoolteacher moves to the Arctic circle and begins receiving letters from 1908."),
    Book(12, "The Grammar of Cities", "Nathaniel Osei", Genre.NONFICTION, 1899, 4.3, "🏙️",
      "What the patterns of streets and stairs tell us about the people who built them."),
    Book(13, "Orbital", "Yoon-seo Kim", Genre.SCIFI, 1599, 4.6, "🛰️",
      "Twelve hours, six astronauts, one small moment that changes everything."),
    Book(14, "The Long Light", "Beatrice Alden", Genre.BIOGRAPHY, 1999, 4.5, "👤",
      "The authorized biography of a reclusive photographer who refused to be known."),
    Book(15, "Paper Crowns", "Ingrid Falk", Genre.FICTION, 1249, 4.0, "👑",
      "A former child queen tries to disappear into an ordinary life in Brussels."),
    Book(16, "The Last Lighthouse Keeper", "Owen Llewellyn", Genre.BIOGRAPHY, 1699, 4.4, "🗼",
      "Fifty years at the edge of the sea, told by the man who lived it."),
    Book(17, "The Copper Thief", "Alma Vasquez", Genre.MYSTERY, 1349, 4.1, "🪙",
      "A series of impossibly small burglaries leads to a much larger secret."),
    Book(18, "After the Fold", "Dmitri Arsenev", Genre.SCIFI, 1749, 4.5, "🌀",
      "When faster-than-light travel becomes possible, space itself files a complaint."),
    Book(19, "How Rivers Remember", "Hana Tanaka", Genre.NONFICTION, 1599, 4.6, "🌊",
      "A natural history of flooding, forgetting, and the maps we draw against both."),
    Book(20, "The Unfinished House", "Rosa Iliev", Genre.FICTION, 1399, 4.3, "🏚️",
      "Three generations, one house, and the renovations nobody will finish."),
  )

  private val byId: Map<Int, Book> = books.associateBy { it.id }

  fun findById(id: Int): Book? = byId[id]

  fun findByIds(ids: List<Int>): List<Book> = ids.mapNotNull { byId[it] }
}
