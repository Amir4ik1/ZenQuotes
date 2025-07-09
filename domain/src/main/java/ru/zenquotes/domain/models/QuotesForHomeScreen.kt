package ru.zenquotes.domain.models

data class QuotesForHomeScreen(
    val quotesList: List<Quote>,
    val quotesOfTheDay: List<Quote>
)