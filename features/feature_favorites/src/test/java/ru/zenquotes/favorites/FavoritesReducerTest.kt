package ru.zenquotes.favorites

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.zenquotes.domain.models.Quote
import ru.zenquotes.favorites.helpers.FavoritesCommand
import ru.zenquotes.favorites.helpers.FavoritesEvents
import ru.zenquotes.favorites.helpers.FavoritesState
import ru.zenquotes.favorites.store.FavoritesReducer

/** Тестирование сущности [Reducer] */
class FavoritesReducerTest {

    private val reducer = FavoritesReducer()
    private val quote = Quote(id = 1, quote = "Some for Test", author = "Anonymous", liked = true)

    /** Проверка поведения срабатывания [FavoritesEvents.DislikeQuote], что после него
     * отработает [FavoritesCommand.Dislike] и состояние загрузки станет [true] */
    @Test
    fun dislikeQuote() {
        val state = FavoritesState()

        val result = reducer.reduce(FavoritesEvents.DislikeQuote(quote), state)

        assertTrue(result.state.isLoading)
        assertTrue(result.commands.any { it is FavoritesCommand.Dislike })
    }

    /** Проверка поведения срабатывания [FavoritesEvents.OnDislikeClicked], что после него
     * успешно обновится лист с цитатами и состояние загрузки станет [false] */
    @Test
    fun afterDislikeScenario() {
        val updated = quote.copy(liked = false)
        val state = FavoritesState(dataList = listOf(quote), isLoading = true)

        val result = reducer.reduce(FavoritesEvents.OnDislikeClicked(updated), state)

        assertEquals(listOf(updated), result.state.dataList)
        assertFalse(result.state.isLoading)
    }

}