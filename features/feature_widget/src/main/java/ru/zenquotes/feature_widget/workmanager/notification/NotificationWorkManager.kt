package ru.zenquotes.feature_widget.workmanager.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import ru.zenquotes.common.callback.MainActivityCallback
import ru.zenquotes.common.result.RequestResult
import ru.zenquotes.common.utils.Constants
import ru.zenquotes.core.datastore.NOTIFICATION_AUTHOR_KEY
import ru.zenquotes.core.datastore.NOTIFICATION_QUOTE_KEY
import ru.zenquotes.core.datastore.dataStore
import ru.zenquotes.core.datastore.saveNotificationQuote
import ru.zenquotes.domain.usecases.home_screen.QuoteUseCase
import ru.zenquotes.feature_widget.R

@HiltWorker
class NotificationWorkManager @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val quoteUseCase: QuoteUseCase,
    private val mainActivityCallback: MainActivityCallback
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Log.d(Constants.WORK_MANAGER_STATUS_NOTIFY, "Work started")
            val response = fetchQuotes(context)
            if (!response) {
                return Result.retry()
            }

            val savedQuote =
                context.dataStore.data.first()[NOTIFICATION_QUOTE_KEY] ?: "Нет сохраненных цитат!"
            val savedAuthor =
                context.dataStore.data.first()[NOTIFICATION_AUTHOR_KEY] ?: "Нет сохраненных цитат!"

            Log.d(
                Constants.WORK_MANAGER_STATUS_NOTIFY,
                "Сохраненная цитата в DataStore: $savedQuote"
            )

            createNotificationChannel()
            createNotification(context, savedQuote, savedAuthor)
            return Result.success()
        } catch (e: Exception) {
            Log.d(Constants.WORK_MANAGER_STATUS_NOTIFY, "Исключение в doWork", e)
            Result.failure()
        }

    }

    private fun createNotificationChannel() {
        val name = "Notification Channel"
        val descriptionText = "Channel for logging reminders about WM"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
        val notificationWorkManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationWorkManager.createNotificationChannel(channel)
    }

    private fun createNotification(context: Context, quote: String, author: String) {

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_view)
        notificationLayout.setTextViewText(R.id.nv_title, quote)
        notificationLayout.setTextViewText(R.id.nv_author, author)

        val intent = Intent(applicationContext, mainActivityCallback::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.format_quote_off_24dp)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomBigContentView(notificationLayout)
            .setCustomContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            NotificationManagerCompat.from(applicationContext)
                .notify(Constants.NOTIFICATION_ID, notification)
        }

    }

    private suspend fun fetchQuotes(context: Context): Boolean {
        return try {
            Log.d(Constants.WORK_MANAGER_STATUS_NOTIFY, "Внутри метода fetchQuotes")
            val response =
                withTimeoutOrNull(5000) {
                    quoteUseCase.getQuote()
                        .filter { it is RequestResult.Success || it is RequestResult.Failure }
                        .first()
                }
            when (response) {
                is RequestResult.Success -> {
                    val quote = response.data.quotesList.getOrNull(1)

                    if (quote != null) {
                        Log.d(Constants.WORK_MANAGER_STATUS_NOTIFY, "Fetched Quote: $quote")
                        context.saveNotificationQuote(quote.quote, quote.author)
                        true
                    } else {
                        Log.d(Constants.WORK_MANAGER_STATUS_NOTIFY, "Quote = null")
                        false
                    }

                }

                is RequestResult.Failure -> {
                    Log.d(
                        Constants.WORK_MANAGER_STATUS_NOTIFY,
                        "Ошибка от fetchQuotes: ${response.message}"
                    )
                    false
                }

                else -> {
                    Log.d(Constants.WORK_MANAGER_STATUS_NOTIFY, "Нет ответа от fetchQuotes")
                    false
                }
            }

        } catch (e: Exception) {
            Log.d(Constants.WORK_MANAGER_STATUS_NOTIFY, "Исключение в fetchQuotes", e)
            false
        }

    }

    @AssistedFactory
    interface Factory {
        fun create(context: Context, params: WorkerParameters): NotificationWorkManager
    }

}