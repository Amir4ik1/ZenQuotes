package ru.zenquotes.quotes.store

import money.vivid.elmslie.core.store.StateReducer
import ru.zenquotes.quotes.helpers.HomeCommand
import ru.zenquotes.quotes.helpers.HomeEffect
import ru.zenquotes.quotes.helpers.HomeEvents
import ru.zenquotes.quotes.helpers.HomeState

class HomeReducer :
    StateReducer<HomeEvents, HomeState, HomeEffect, HomeCommand>() {
    override fun Result.reduce(event: HomeEvents) = when (event) {
        HomeEvents.Init -> {
            state { copy(isLoading = true) }
            commands { +HomeCommand.LoadQuotes }
        }

        is HomeEvents.QuotesForHomeScreenLoaded -> {
            state {
                copy(
                    dataList = event.dataList,
                    quote = event.quote,
                    isLoading = event.isLoading,
                    error = event.error
                )
            }
        }

        is HomeEvents.LikeQuote -> {
            state { copy(isLoading = true) }
            commands { +HomeCommand.Like(event.quote) }
        }

        is HomeEvents.OnLikeClicked -> {
            state {
                copy(
                    dataList = dataList.map { quote ->
                        if (quote.id == event.quote.id) event.quote else quote
                    }.toMutableList(),
                    isLoading = false
                )
            }
        }

        HomeEvents.Retry -> {
            state { copy(isLoading = true) }
            commands { +HomeCommand.Retry }
        }

        is HomeEvents.AfterRetry -> {
            state {
                copy(
                    dataList = event.dataList,
                    quote = event.quote,
                    isLoading = event.isLoading
                )
            }
        }

        HomeEvents.Loading -> {
            state {
                copy(
                    isLoading = true,
                    dataList = mutableListOf(),
                    error = "Data is loading, please wait"
                )
            }
            effects { +HomeEffect.ShowToast(state.error) }
        }

        is HomeEvents.Error -> {
            state { copy(isLoading = false) }
            effects { +event.throwable.message?.let { HomeEffect.ShowToast(it) } }
        }
    }
}