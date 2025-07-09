package ru.zenquotes.favorites

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.zenquotes.domain.models.Quote
import ru.zenquotes.domain.usecases.favorite_screen.FavoriteLikedQuote
import ru.zenquotes.domain.usecases.favorite_screen.FavoriteQuoteUseCase
import ru.zenquotes.domain.usecases.favorite_screen.GetFavoriteQuote
import ru.zenquotes.favorites.helpers.FavoritesCommand
import ru.zenquotes.favorites.helpers.FavoritesEvents
import ru.zenquotes.favorites.store.FavoritesActor

/** Тестирование сущности [Actor] (аналог [ViewModel]) */
@ExperimentalCoroutinesApi
class FavoritesActorTest {

    private val favLikedQuote = mockk<FavoriteLikedQuote>()
    private val getFavoriteQuote = mockk<GetFavoriteQuote>()
    private val useCase = FavoriteQuoteUseCase(getFavoriteQuote, favLikedQuote)
    private val actor = FavoritesActor(useCase)

    /** Проверка, что после выполнения [FavoritesCommand.Dislike]
     * успешно вызовется [FavoritesEvents.OnDislikeClicked] с обновленной цитатой */
    @Test
    fun returnResultOfDislikeClick() = runTest {
        val quote = Quote(id = 1, quote = "Some some test", author = "Antonio", liked = true)
        val updatedQuote = quote.copy(liked = false)
        coEvery { favLikedQuote.invoke(quote) } returns updatedQuote

        val result = actor.execute(FavoritesCommand.Dislike(quote)).first()

        assertTrue(result is FavoritesEvents.OnDislikeClicked && result.quote == updatedQuote)
    }

    /** Проверка, что после выполнения [FavoritesCommand.LoadFavoriteQuotes]
     * вернется ошибка */
    @Test
    fun returnErrorAfterLoading() = runTest {
        val exception = RuntimeException("Test error")
        every { getFavoriteQuote.invoke("") } returns flow { throw exception }

        val events = actor.execute(FavoritesCommand.LoadFavoriteQuotes).toList()

        assertTrue(
            events.any {
                it is FavoritesEvents.Error && it.throwable.message == "Test error"
            }
        )
    }

    /** Проверка, что после выполнения [FavoritesCommand.Search]
     * вызовется [FavoritesEvents.OnSearchCompleted] с найденным списком */
    @Test
    fun searchIsSuccessful() = runTest {
        val query = "A2"
        val quotes = listOf(
            Quote(id = 312, quote = "QQQQQ", author = "A2", liked = true)
        )
        every { getFavoriteQuote.invoke(query) } returns flowOf(quotes)

        val events = actor.execute(FavoritesCommand.Search(query)).toList()

        assertTrue(events.any {
            it is FavoritesEvents.OnSearchCompleted && it.quotes == quotes && it.query == query && !it.isLoading
        })
    }
}