package ru.zenquotes.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.zenquotes.common.result.RequestResult
import ru.zenquotes.domain.models.Quote
import ru.zenquotes.domain.models.QuotesForHomeScreen

interface QuoteRepository {

    fun getQuote(): Flow<RequestResult<QuotesForHomeScreen>>

    suspend fun saveLikedQuote(quote: Quote)

}