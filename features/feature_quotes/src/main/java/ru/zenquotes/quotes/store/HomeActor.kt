package ru.zenquotes.quotes.store

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import money.vivid.elmslie.core.store.Actor
import ru.zenquotes.common.result.RequestResult
import ru.zenquotes.domain.usecases.home_screen.QuoteUseCase
import ru.zenquotes.quotes.helpers.HomeCommand
import ru.zenquotes.quotes.helpers.HomeEvents
import javax.inject.Inject

class HomeActor @Inject constructor(
    private val getQuoteUseCase: QuoteUseCase
) : Actor<HomeCommand, HomeEvents>() {
    override fun execute(command: HomeCommand): Flow<HomeEvents> = when (command) {
        HomeCommand.LoadQuotes -> getQuoteUseCase.getQuote()
            .mapEvents(
                { result ->
                    when (result) {
                        is RequestResult.Success -> {
                            result.data.let { data ->
                                HomeEvents.QuotesForHomeScreenLoaded(
                                    dataList = data.quotesList.toMutableList(),
                                    quote = data.quotesOfTheDay.first(),
                                    isLoading = false,
                                    error = ""
                                )
                            }
                        }

                        is RequestResult.Failure -> {
                            result.throwable?.let { HomeEvents.Error(throwable = it) }
                        }

                        RequestResult.Loading -> {
                            HomeEvents.Loading
                        }

                    }
                },
                HomeEvents::Error
            )

        is HomeCommand.Like -> flow {
            val updatedQuote = getQuoteUseCase.likedQuote(command.quote)
            /** Вызываем небольшую задержку, чтоб успела поменяться иконка у элемента цитаты после нажатия */
            delay(30)
            emit(HomeEvents.OnLikeClicked(updatedQuote))
        }

        HomeCommand.Retry -> getQuoteUseCase.getQuote()
            .mapEvents(
                { result ->
                    when (result) {
                        is RequestResult.Success -> {
                            result.data.let { data ->
                                HomeEvents.AfterRetry(
                                    dataList = data.quotesList.toMutableList(),
                                    quote = data.quotesOfTheDay.first(),
                                    isLoading = false
                                )
                            }
                        }

                        is RequestResult.Failure -> {
                            result.throwable?.let { HomeEvents.Error(throwable = it) }
                        }

                        RequestResult.Loading -> {
                            HomeEvents.Loading
                        }

                    }
                },
                HomeEvents::Error
            )
    }

}