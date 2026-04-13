package com.mattbobambrose.html

import com.mattbobambrose.data.Catalog
import com.mattbobambrose.model.Order
import com.mattbobambrose.model.lineSubtotalDisplay
import io.ktor.server.application.ApplicationCall
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.section
import kotlinx.html.span
import kotlinx.html.ul

private val orderFormatter: DateTimeFormatter =
  DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").withZone(ZoneId.systemDefault())

suspend fun ApplicationCall.renderOrderHistory(orders: List<Order>) {
  respondPage("Orders") {
    h1(classes = "text-3xl font-bold mb-6") { +"Your orders" }
    if (orders.isEmpty()) {
      section(classes = "rounded border border-dashed border-slate-300 p-10 text-center") {
        attributes["data-testid"] = "orders-empty"
        p(classes = "text-slate-500") { +"You haven't placed any orders yet." }
        a(href = "/catalog", classes = "mt-3 inline-block text-indigo-700 hover:underline") { +"Start browsing →" }
      }
    } else {
      div(classes = "space-y-4") {
        attributes["data-testid"] = "orders-list"
        orders.sortedByDescending { it.placedAt }.forEach { order ->
          section(classes = "bg-white rounded-xl border border-slate-200 p-5") {
            attributes["data-testid"] = "order-${order.id}"
            div(classes = "flex items-center justify-between") {
              h2(classes = "font-semibold text-slate-900") { +order.id }
              span(classes = "text-sm text-slate-500") { +orderFormatter.format(order.placedAt) }
            }
            ul(classes = "mt-3 space-y-1 text-sm") {
              order.items.forEach { item ->
                val book = Catalog.findById(item.bookId) ?: return@forEach
                li(classes = "flex justify-between") {
                  span { +"${book.title} × ${item.qty}" }
                  span(classes = "text-slate-700") { +lineSubtotalDisplay(book, item.qty) }
                }
              }
            }
            div(classes = "mt-3 pt-3 border-t border-slate-100 flex justify-between font-semibold") {
              span { +"Total" }
              span { +order.totalDisplay }
            }
          }
        }
      }
    }
  }
}
