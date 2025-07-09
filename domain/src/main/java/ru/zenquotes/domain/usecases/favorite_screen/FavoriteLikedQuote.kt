package ru.zenquotes.domain.usecases.favorite_screen

import ru.zenquotes.domain.models.Quote
import ru.zenquotes.domain.repositories.FavoriteQuoteRepository
import javax.inject.Inject

class FavoriteLikedQuote @Inject constructor(private val quoteRepository: FavoriteQuoteRepository) {

    suspend operator fun invoke(quote: Quote): Quote {
        val updatedQuote = quote.copy(liked = !quote.liked)
        quoteRepository.saveLikedQuote(updatedQuote)
        return updatedQuote
    }
}