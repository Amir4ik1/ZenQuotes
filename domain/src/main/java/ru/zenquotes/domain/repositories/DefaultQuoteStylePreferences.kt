package ru.zenquotes.domain.repositories

import ru.zenquotes.common.utils.QuoteStyle

interface DefaultQuoteStylePreferences {
    fun saveDefaultQuoteStyle(quoteStyle: QuoteStyle)
    fun getDefaultQuoteStyle(): QuoteStyle
}