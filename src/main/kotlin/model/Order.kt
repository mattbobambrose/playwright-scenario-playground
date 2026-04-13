package com.mattbobambrose.model

import java.time.Instant

data class Order(
  val id: String,
  val username: String,
  val items: List<CartItem>,
  val totalCents: Int,
  val shippingName: String,
  val shippingAddress: String,
  val placedAt: Instant,
) {
  val totalDisplay: String get() = formatCents(totalCents)
}
