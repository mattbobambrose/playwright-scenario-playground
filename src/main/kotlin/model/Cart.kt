package com.mattbobambrose.model

data class CartItem(
  val bookId: Int,
  val qty: Int,
)

class Cart {
  private val items = mutableListOf<CartItem>()

  @Synchronized
  fun snapshot(): List<CartItem> = items.toList()

  @Synchronized
  fun add(bookId: Int, qty: Int = 1) {
    val idx = items.indexOfFirst { it.bookId == bookId }
    if (idx == -1) items += CartItem(bookId, qty)
    else items[idx] = items[idx].copy(qty = items[idx].qty + qty)
  }

  @Synchronized
  fun setQty(bookId: Int, qty: Int) {
    val idx = items.indexOfFirst { it.bookId == bookId }
    if (idx != -1) {
      if (qty <= 0) items.removeAt(idx)
      else items[idx] = items[idx].copy(qty = qty)
    }
  }

  @Synchronized
  fun remove(bookId: Int) {
    items.removeAll { it.bookId == bookId }
  }

  @Synchronized
  fun clear() {
    items.clear()
  }

  @get:Synchronized
  val itemCount: Int get() = items.sumOf { it.qty }
}
