package ru.zenquotes.feature_widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ru.zenquotes.core.datastore.WIDGET_QUOTE_KEY
import ru.zenquotes.core.datastore.dataStore
import ru.zenquotes.core.datastore.saveWidgetQuoteToDataStore

@Composable
fun QuoteWidget(savedQuote: String) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Black)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            provider = ImageProvider(R.drawable.format_quote_off_24dp),
            contentDescription = null,
            modifier = GlanceModifier
                .padding(start = 12.dp, top = 10.dp)
                .size(30.dp)
        )

        Text(
            text = savedQuote,
            style = TextStyle(
                fontSize = 18.sp,
                color = ColorProvider(Color.White),
                fontWeight = FontWeight.Normal,
            ),
            modifier = GlanceModifier.wrapContentSize()
                .padding(horizontal = 15.dp, vertical = 15.dp)
        )
    }
}

object QuotesWidgetObject : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val savedQuote = runBlocking {
            context.dataStore.data.first()[WIDGET_QUOTE_KEY]
                ?: ("Виджет обновляется. Обновится через некоторое время или попробуйте перезагрузить устройство")
        }

        Log.d("WID,", "quote $savedQuote")

        provideContent {
            QuoteWidget(savedQuote)
        }

    }
}

class QuotesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = QuotesWidgetObject

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d("WorkManagerStatus", "Виджет активирован, запланировано обновление")
    }

}

suspend fun Context.saveWidgetQuoteAndNotify(quote: String) {
    saveWidgetQuoteToDataStore(quote)
    val intent = Intent(this, QuotesWidgetReceiver::class.java).apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }
    sendBroadcast(intent)
}