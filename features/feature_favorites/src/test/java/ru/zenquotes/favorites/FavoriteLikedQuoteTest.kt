package ru.zenquotes.favorites

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.zenquotes.domain.models.Quote
import ru.zenquotes.domain.repositories.FavoriteQuoteRepository
import ru.zenquotes.domain.usecases.favorite_screen.FavoriteLikedQuote

@ExperimentalCoroutinesApi
class FavoriteLikedQuoteTest {

    private val repository = mockk<FavoriteQuoteRepository>(relaxed = true)
    private val useCase = FavoriteLikedQuote(repository)

    /** Проверка, что [Лайк] по итогу вызывает сохранение обновленной цитаты */
    @Test
    fun likedQuote() = runTest {
        val quote = Quote(id = 1, quote = "Some changes improve", author = "Elon Musk", liked = false)
        val expected = quote.copy(liked = true)

        val result = useCase.invoke(quote)

        coVerify { repository.saveLikedQuote(expected) }
        assertEquals(expected, result)
    }
}