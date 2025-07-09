package ru.zenquotes.data.repository

import kotlinx.coroutines.flow.Flow
import ru.zenquotes.data.local.QuoteDataBase
import ru.zenquotes.domain.models.Quote
import ru.zenquotes.domain.repositories.FavoriteQuoteRepository

class FavoriteQuoteRepositoryImpl(private val db: QuoteDataBase) : FavoriteQuoteRepository {

    override fun getAllLikedQuotes(query: String): Flow<List<Quote>> =
        if (query.isNotBlank()) {
            db.getQuoteDao().searchForQuotes(query)
        } else {
            db.getQuoteDao().getAllLikedQuotes()
        }

    override suspend fun saveLikedQuote(quote: Quote) = db.getQuoteDao().insertLikedQuote(quote)
}