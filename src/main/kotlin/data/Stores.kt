package com.mattbobambrose.data

import com.mattbobambrose.model.Cart
import com.mattbobambrose.model.Order
import com.mattbobambrose.model.Shelves
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong

object Stores {
  private val carts = ConcurrentHashMap<String, Cart>()
  private val shelves = ConcurrentHashMap<String, Shelves>()
  private val orders = ConcurrentHashMap<String, CopyOnWriteArrayList<Order>>()
  private val orderById = ConcurrentHashMap<String, Order>()
  private val orderSeq = AtomicLong(1000)

  fun cartFor(key: String): Cart = carts.computeIfAbsent(key) { Cart() }

  fun cartCountFor(key: String?): Int = key?.let { carts[it]?.itemCount } ?: 0

  fun shelvesFor(username: String): Shelves = shelves.computeIfAbsent(username) { Shelves() }

  fun ordersFor(username: String): List<Order> =
    orders[username]?.toList().orEmpty()

  fun addOrder(order: Order) {
    orders.computeIfAbsent(order.username) { CopyOnWriteArrayList() }.add(order)
    orderById[order.id] = order
  }

  fun findOrder(id: String): Order? = orderById[id]

  fun nextOrderId(): String = "ORD-${orderSeq.incrementAndGet()}"

  fun resetAll() {
    carts.clear()
    shelves.clear()
    orders.clear()
    orderById.clear()
    orderSeq.set(1000)
  }
}
