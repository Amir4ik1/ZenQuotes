package ru.zenquotes.quotes.helpers

import ru.zenquotes.domain.models.Quote

sealed interface HomeEvents {
    data object Init : HomeEvents
    class QuotesForHomeScreenLoaded(
        val dataList: MutableList<Quote>,
        val quote: Quote,
        val isLoading: Boolean,
        val error: String
    ) : HomeEvents

    class LikeQuote(val quote: Quote) : HomeEvents
    class OnLikeClicked(val quote: Quote) : HomeEvents

    data object Retry : HomeEvents
    class AfterRetry(
        val dataList: MutableList<Quote>,
        val quote: Quote,
        val isLoading: Boolean,
    ) : HomeEvents

    data object Loading : HomeEvents
    class Error(val throwable: Throwable) : HomeEvents
}