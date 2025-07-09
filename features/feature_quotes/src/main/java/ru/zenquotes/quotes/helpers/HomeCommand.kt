package ru.zenquotes.quotes.helpers

import ru.zenquotes.domain.models.Quote

sealed interface HomeCommand {
    data object LoadQuotes : HomeCommand
    class Like(val quote: Quote) : HomeCommand
    data object Retry : HomeCommand
}