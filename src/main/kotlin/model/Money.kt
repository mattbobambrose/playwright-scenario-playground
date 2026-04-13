package com.mattbobambrose.model

fun formatCents(cents: Int): String = "$%,.2f".format(cents / 100.0)

fun lineSubtotalDisplay(book: Book, qty: Int): String = formatCents(book.priceCents * qty)
