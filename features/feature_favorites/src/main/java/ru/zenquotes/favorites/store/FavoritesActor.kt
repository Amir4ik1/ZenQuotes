package ru.zenquotes.favorites.store

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import money.vivid.elmslie.core.store.Actor
import ru.zenquotes.domain.usecases.favorite_screen.FavoriteQuoteUseCase
import ru.zenquotes.favorites.helpers.FavoritesCommand
import ru.zenquotes.favorites.helpers.FavoritesEvents
import javax.inject.Inject

class FavoritesActor @Inject constructor(
    private val favoriteQuoteUseCase: FavoriteQuoteUseCase
) : Actor<FavoritesCommand, FavoritesEvents>() {
    override fun execute(command: FavoritesCommand): Flow<FavoritesEvents> = when (command) {
        FavoritesCommand.LoadFavoriteQuotes -> favoriteQuoteUseCase.getFavoriteQuote("")
            .mapEvents(
                { dataList ->
                    FavoritesEvents.FavoriteQuotesLoaded(quotes = dataList, isLoading = false)
                },
                FavoritesEvents::Error
            )

        is FavoritesCommand.Dislike -> flow {
            val updatedQuote = favoriteQuoteUseCase.favLikedQuote(command.quote)
            emit(FavoritesEvents.OnDislikeClicked(updatedQuote))
        }

        is FavoritesCommand.Search -> favoriteQuoteUseCase.getFavoriteQuote(command.query)
            .mapEvents(
                { dataList ->
                    FavoritesEvents.OnSearchCompleted(quotes = dataList, query = command.query, isLoading = false)
                },
                FavoritesEvents::Error
            )

        FavoritesCommand.Refresh -> flow {
            val dataList = favoriteQuoteUseCase.getFavoriteQuote("").first()
            /** Показываем задержку, чтоб в течение рекомпозиции изменилось состояние state.isRefreshing */
            /** Поскольку данные из БД забираются очень быстро и состояние state не успевает измениться */
            delay(600)
            emit(FavoritesEvents.AfterRefresh(quotes = dataList, isLoading = false, isRefreshing = false))
        }

    }
}