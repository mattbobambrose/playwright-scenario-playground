package com.mattbobambrose.model

enum class Shelf(val display: String) {
  WANT("Want to Read"),
  READING("Reading"),
  FINISHED("Finished");

  companion object {
    fun fromParam(value: String?): Shelf? =
      value?.let { runCatching { valueOf(it.uppercase()) }.getOrNull() }
  }
}

class Shelves {
  private val lists: Map<Shelf, MutableList<Int>> =
    Shelf.entries.associateWith { mutableListOf() }

  @Synchronized
  fun booksOn(shelf: Shelf): List<Int> = lists.getValue(shelf).toList()

  @Synchronized
  fun addIfAbsent(bookId: Int, shelf: Shelf) {
    if (Shelf.entries.any { bookId in lists.getValue(it) }) return
    lists.getValue(shelf).add(bookId)
  }

  @Synchronized
  fun move(bookId: Int, from: Shelf, to: Shelf, beforeBookId: Int?) {
    val src = lists.getValue(from)
    if (!src.remove(bookId)) return
    val dest = lists.getValue(to)
    val insertAt =
      if (beforeBookId == null) dest.size
      else dest.indexOf(beforeBookId).let { if (it == -1) dest.size else it }
    dest.add(insertAt, bookId)
  }

  @Synchronized
  fun reorder(shelf: Shelf, bookId: Int, beforeBookId: Int?) {
    val list = lists.getValue(shelf)
    if (!list.remove(bookId)) return
    val insertAt =
      if (beforeBookId == null) list.size
      else list.indexOf(beforeBookId).let { if (it == -1) list.size else it }
    list.add(insertAt, bookId)
  }
}
