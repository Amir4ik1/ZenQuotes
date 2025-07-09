package ru.zenquotes.domain.usecases.home_screen

import ru.zenquotes.domain.models.Quote
import ru.zenquotes.domain.repositories.QuoteRepository
import javax.inject.Inject

class LikedQuote @Inject constructor(private val quoteRepository: QuoteRepository) {

    suspend operator fun invoke(quote: Quote): Quote {
        val updatedQuote = quote.copy(liked = !quote.liked)
        quoteRepository.saveLikedQuote(updatedQuote)
        return updatedQuote
    }
}