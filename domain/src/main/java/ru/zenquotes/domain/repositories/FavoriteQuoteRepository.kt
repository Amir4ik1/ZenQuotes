package ru.zenquotes.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.zenquotes.domain.models.Quote

interface FavoriteQuoteRepository {

    fun getAllLikedQuotes(query: String): Flow<List<Quote>>

    suspend fun saveLikedQuote(quote: Quote)
}