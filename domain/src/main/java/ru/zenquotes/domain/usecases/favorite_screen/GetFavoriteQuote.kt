package ru.zenquotes.domain.usecases.favorite_screen

import kotlinx.coroutines.flow.Flow
import ru.zenquotes.domain.models.Quote
import ru.zenquotes.domain.repositories.FavoriteQuoteRepository
import javax.inject.Inject

class GetFavoriteQuote @Inject constructor(private val quoteRepository: FavoriteQuoteRepository) {

    operator fun invoke(query: String): Flow<List<Quote>> = quoteRepository.getAllLikedQuotes(query)
}