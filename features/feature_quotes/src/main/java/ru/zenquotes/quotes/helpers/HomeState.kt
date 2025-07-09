package ru.zenquotes.quotes.helpers

import ru.zenquotes.domain.models.Quote

data class HomeState(
    val dataList: MutableList<Quote> = mutableListOf(),
    val quote: Quote? = null,
    val liked: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = ""
)