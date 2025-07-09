package ru.zenquotes.domain.usecases.favorite_screen

data class FavoriteQuoteUseCase(
    val getFavoriteQuote: GetFavoriteQuote,
    val favLikedQuote: FavoriteLikedQuote
)
