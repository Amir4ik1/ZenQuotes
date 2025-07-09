package ru.zenquotes.favorites.store

import money.vivid.elmslie.core.store.StateReducer
import ru.zenquotes.favorites.helpers.FavoritesCommand
import ru.zenquotes.favorites.helpers.FavoritesEffect
import ru.zenquotes.favorites.helpers.FavoritesEvents
import ru.zenquotes.favorites.helpers.FavoritesState

class FavoritesReducer :
    StateReducer<FavoritesEvents, FavoritesState, FavoritesEffect, FavoritesCommand>() {
    override fun Result.reduce(event: FavoritesEvents) = when (event) {
        FavoritesEvents.Init -> {
            state { copy(isLoading = true) }
            commands {
                +FavoritesCommand.LoadFavoriteQuotes
            }
        }

        is FavoritesEvents.FavoriteQuotesLoaded -> {
            state { copy(dataList = event.quotes, isLoading = event.isLoading) }
        }

        is FavoritesEvents.DislikeQuote -> {
            state { copy(isLoading = true) }
            commands { +FavoritesCommand.Dislike(event.quote) }
        }

        is FavoritesEvents.OnDislikeClicked -> {
            state {
                copy(dataList = dataList.map { quote ->
                    if (quote.id == event.quote.id) event.quote else quote
                }, isLoading = false)
            }
        }

        is FavoritesEvents.OnSearchQueryChanged -> {
            state { copy(query = event.query, isLoading = true) }
            commands {
                +FavoritesCommand.Search(event.query)
            }
        }

        is FavoritesEvents.OnSearchCompleted -> {
            state {
                copy(
                    dataList = event.quotes,
                    query = event.query,
                    isLoading = event.isLoading
                )
            }
        }

        FavoritesEvents.ClearSearchField -> {
            state { copy(query = "") }
        }

        FavoritesEvents.Refresh -> {
            state { copy(isLoading = true, isRefreshing = true) }
            commands { +FavoritesCommand.Refresh }
        }

        is FavoritesEvents.AfterRefresh -> {
            state {
                copy(
                    dataList = event.quotes,
                    isLoading = event.isLoading,
                    isRefreshing = event.isRefreshing
                )
            }
        }

        is FavoritesEvents.Error -> {
            state { copy(isLoading = false) }
            effects { +event.throwable.message?.let { FavoritesEffect.ShowToast(it) } }
        }
    }
}