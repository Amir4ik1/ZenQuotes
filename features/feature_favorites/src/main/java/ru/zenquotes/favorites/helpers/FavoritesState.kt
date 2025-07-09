package ru.zenquotes.favorites.helpers

import androidx.compose.runtime.Immutable
import ru.zenquotes.domain.models.Quote

@Immutable
data class FavoritesState(
    val dataList: List<Quote> = emptyList(),
    val liked: Boolean = false,
    val query: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String = ""
)
