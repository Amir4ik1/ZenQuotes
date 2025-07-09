package ru.zenquotes.favorites.helpers

sealed interface FavoritesEffect {
    data class ShowToast(val message: String) : FavoritesEffect
}