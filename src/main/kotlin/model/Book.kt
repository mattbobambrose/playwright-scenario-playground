package com.mattbobambrose.model

data class Book(
  val id: Int,
  val title: String,
  val author: String,
  val genre: Genre,
  val priceCents: Int,
  val rating: Double,
  val coverEmoji: String,
  val blurb: String,
) {
  val priceDisplay: String get() = formatCents(priceCents)
}

enum class Genre(val display: String) {
  FICTION("Fiction"),
  NONFICTION("Nonfiction"),
  SCIFI("Sci-Fi"),
  MYSTERY("Mystery"),
  BIOGRAPHY("Biography");

  companion object {
    fun fromParam(value: String?): Genre? =
      value?.let { runCatching { valueOf(it.uppercase()) }.getOrNull() }
  }
}
