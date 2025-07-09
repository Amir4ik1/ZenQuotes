package ru.zenquotes.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("quote_prefs")


val WIDGET_QUOTE_KEY = stringPreferencesKey("widgetQuote")
val NOTIFICATION_QUOTE_KEY = stringPreferencesKey("notificationQuote")
val NOTIFICATION_AUTHOR_KEY = stringPreferencesKey("notificationAuthor")

suspend fun Context.saveWidgetQuoteToDataStore(quote: String) {
    dataStore.edit { preferences ->
        preferences[WIDGET_QUOTE_KEY] = quote
    }
}

suspend fun Context.saveNotificationQuote(quote: String, author: String) =
    dataStore.edit { preferences ->
        preferences[NOTIFICATION_QUOTE_KEY] = quote
        preferences[NOTIFICATION_AUTHOR_KEY] = author
    }

fun Context.getSavedNotificationQuote(): Flow<String> = dataStore.data.map { preferences ->
    preferences[NOTIFICATION_QUOTE_KEY] ?: "No quote saved yet..."
    preferences[NOTIFICATION_AUTHOR_KEY] ?: "No author saved yet..."
}

fun Context.getSavedWidgetQuote(): Flow<String> = dataStore.data.map { preferences ->
    preferences[WIDGET_QUOTE_KEY] ?: "No quote saved yet..."
}