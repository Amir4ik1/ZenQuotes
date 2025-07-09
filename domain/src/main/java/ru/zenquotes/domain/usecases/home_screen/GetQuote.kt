package ru.zenquotes.domain.usecases.home_screen

import kotlinx.coroutines.flow.Flow
import ru.zenquotes.common.result.RequestResult
import ru.zenquotes.domain.models.QuotesForHomeScreen
import ru.zenquotes.domain.repositories.QuoteRepository
import javax.inject.Inject

class GetQuote @Inject constructor(private val quoteRepository: QuoteRepository) {

    operator fun invoke(): Flow<RequestResult<QuotesForHomeScreen>> = quoteRepository.getQuote()
}