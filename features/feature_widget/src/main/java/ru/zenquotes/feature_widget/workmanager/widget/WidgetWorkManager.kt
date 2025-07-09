package ru.zenquotes.feature_widget.workmanager.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import ru.zenquotes.common.result.RequestResult
import ru.zenquotes.core.datastore.WIDGET_QUOTE_KEY
import ru.zenquotes.core.datastore.dataStore
import ru.zenquotes.domain.usecases.home_screen.QuoteUseCase
import ru.zenquotes.feature_widget.QuotesWidgetObject
import ru.zenquotes.feature_widget.saveWidgetQuoteAndNotify

@HiltWorker
class WidgetWorkManager @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val quoteUseCase: QuoteUseCase
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            Log.d("WorkManagerStatus", "Work started")
            val response = fetchQuotes(context)
            if (!response) {
                return Result.retry()
            }
            val savedQuote =
                context.dataStore.data.first()[WIDGET_QUOTE_KEY] ?: "Нет сохраненных цитат!"

            Log.d("WorkManagerStatus", "Сохраненная цитата в DataStore: $savedQuote")

            val glanceAppWidgetManager = GlanceAppWidgetManager(applicationContext)
            val widgetIds = glanceAppWidgetManager.getGlanceIds(QuotesWidgetObject::class.java)

            if (widgetIds.isNotEmpty()) {
                widgetIds.forEach { _ ->
                    QuotesWidgetObject.updateAll(applicationContext)
                }
            }

            return Result.success()

        } catch (e: Exception) {
            Log.d("WorkManagerStatus", "Исключение в doWork", e)
            Result.failure()
        }

    }

    private suspend fun fetchQuotes(context: Context): Boolean {
        return try {
            Log.d("WorkManagerStatus", "Внутри метода fetchQuotes")
            val response =
                withTimeoutOrNull(7000) {
                    quoteUseCase.getQuote()
                        .filter { it is RequestResult.Success || it is RequestResult.Failure }
                        .first()
                }
            when (response) {
                is RequestResult.Success -> {
                    val quote = response.data.quotesList.getOrNull(0)?.quote

                    if (quote != null) {
                        Log.d("WorkManagerStatus", "Fetched Quote: $quote")
                        context.saveWidgetQuoteAndNotify(quote)
                        true
                    } else {
                        Log.d("WorkManagerStatus", "Quote is null")
                        false
                    }

                }

                is RequestResult.Failure -> {
                    Log.d("WorkManagerStatus", "Ошибка от fetchQuotes: ${response.message}")
                    false
                }

                else -> {
                    Log.d("WorkManagerStatus", "Нет ответа от fetchQuotes")
                    false
                }
            }
        } catch (e: Exception) {
            Log.d("WorkManagerStatus", "Исключение в fetchQuotes", e)
            false
        }

    }

    @AssistedFactory
    interface Factory {
        fun create(context: Context, params: WorkerParameters): WidgetWorkManager
    }

}