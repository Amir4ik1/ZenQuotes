package ru.zenquotes.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import ru.zenquotes.common.result.RequestResult
import ru.zenquotes.data.local.QuoteDataBase
import ru.zenquotes.data.mapper.toQuote
import ru.zenquotes.data.remote.QuoteApi
import ru.zenquotes.domain.models.Quote
import ru.zenquotes.domain.models.QuotesForHomeScreen
import ru.zenquotes.domain.repositories.QuoteRepository
import java.io.IOException

class QuoteRepositoryImpl(private val api: QuoteApi, private val db: QuoteDataBase) :
    QuoteRepository {

    override fun getQuote(): Flow<RequestResult<QuotesForHomeScreen>> = flow {
        emit(RequestResult.Loading)

        try {
            coroutineScope {
                val quotesListDeferred = async { api.getQuotesList().map { it.toQuote() } }
                val quoteOfTheDayDeferred = async { api.getQuoteOfTheDay().map { it.toQuote() } }

                val currentList = db.getQuoteDao().getAllQuotes()

                val notLikedQuotes = currentList.filter { !it.liked }
                if (notLikedQuotes.isNotEmpty()) {
                    db.getQuoteDao().deleteQuoteList(notLikedQuotes)
                }

                val quotesList = quotesListDeferred.await()
                db.getQuoteDao().insertQuoteList(quotesList)

                val quoteOfTheDay = quoteOfTheDayDeferred.await()

                val allQuotes = db.getQuoteDao().getAllQuotes()

                emit(
                    RequestResult.Success(
                        QuotesForHomeScreen(
                            quotesList = allQuotes,
                            quotesOfTheDay = quoteOfTheDay
                        )
                    )
                )
            }
        } catch (error: Throwable) {
            val errorMessage = when (error) {
                is IOException -> "Нет интернета. Пожалуйста, попробуйте позднее."
                is HttpException -> when (error.code()) {
                    400 -> "Bad Request"
                    401 -> "Unauthorized"
                    403 -> "Forbidden"
                    429 -> "Слишком много запросов к серверу! Пожалуйста, вернитесь обратно через некоторое время..."
                    else -> "Unknown error ${error.message()}"
                }
                else -> "Что-то пошло не так. Пожалуйста, попробуйте позднее."
            }
            emit(RequestResult.Failure(errorMessage, error))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun saveLikedQuote(quote: Quote) {
        db.getQuoteDao().insertLikedQuote(quote)
    }


}