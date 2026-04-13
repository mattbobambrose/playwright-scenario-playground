package com.mattbobambrose.model

enum class SortOrder(val param: String, val display: String) {
  TITLE("title", "Title (A–Z)"),
  PRICE_ASC("priceAsc", "Price (low → high)"),
  PRICE_DESC("priceDesc", "Price (high → low)"),
  RATING("rating", "Rating (high → low)");

  companion object {
    val default = TITLE
    fun fromParam(value: String?): SortOrder =
      entries.firstOrNull { it.param == value } ?: default
  }
}
