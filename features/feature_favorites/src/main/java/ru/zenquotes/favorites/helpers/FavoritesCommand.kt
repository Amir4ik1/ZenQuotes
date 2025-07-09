package ru.zenquotes.favorites.helpers

import ru.zenquotes.domain.models.Quote

sealed interface FavoritesCommand {
    data object LoadFavoriteQuotes : FavoritesCommand
    class Dislike(val quote: Quote) : FavoritesCommand
    class Search(val query: String) : FavoritesCommand
    data object Refresh : FavoritesCommand
}