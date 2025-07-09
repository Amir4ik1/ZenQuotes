package ru.zenquotes.favorites.helpers

import ru.zenquotes.domain.models.Quote

sealed interface FavoritesEvents {
    data object Init : FavoritesEvents
    class FavoriteQuotesLoaded(
        val quotes: List<Quote>,
        val isLoading: Boolean
    ) : FavoritesEvents

    class DislikeQuote(val quote: Quote) : FavoritesEvents
    class OnDislikeClicked(val quote: Quote) : FavoritesEvents

    class OnSearchQueryChanged(val query: String) : FavoritesEvents
    class OnSearchCompleted(
        val quotes: List<Quote>,
        val query: String,
        val isLoading: Boolean
    ) : FavoritesEvents

    data object ClearSearchField : FavoritesEvents

    data object Refresh : FavoritesEvents
    class AfterRefresh(
        val quotes: List<Quote>,
        val isLoading: Boolean,
        val isRefreshing: Boolean
    ) : FavoritesEvents

    class Error(val throwable: Throwable) : FavoritesEvents
}