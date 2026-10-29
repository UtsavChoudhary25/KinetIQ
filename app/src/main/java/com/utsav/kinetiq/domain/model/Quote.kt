package com.utsav.kinetiq.domain.model

enum class QuoteCategory(val label: String, val emoji: String) {
    DISCIPLINE("DISCIPLINE", "🏋️"),
    MINDSET("MINDSET", "🧠"),
    CONSISTENCY("CONSISTENCY", "🔥"),
    PERFORMANCE("PERFORMANCE", "🏆")
}

data class Quote(
    val text: String,
    val author: String,
    val category: QuoteCategory
)
