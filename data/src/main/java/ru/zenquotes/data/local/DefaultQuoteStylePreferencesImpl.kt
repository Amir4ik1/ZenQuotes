package ru.zenquotes.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import ru.zenquotes.common.utils.QuoteStyle
import ru.zenquotes.domain.repositories.DefaultQuoteStylePreferences
import javax.inject.Inject

class DefaultQuoteStylePreferencesImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
): DefaultQuoteStylePreferences {

    override fun saveDefaultQuoteStyle(quoteStyle: QuoteStyle) {
        val quoteStyleString = when (quoteStyle) {
            is QuoteStyle.DefaultTheme -> "DefaultTheme"
            is QuoteStyle.CodeSnippetTheme -> "CodeSnippetTheme"
            is QuoteStyle.SpotifyTheme -> "SpotifyTheme"
        }
        sharedPreferences.edit {
            putString(QUOTE_STYLE_KEY, quoteStyleString)
        }
    }

    override fun getDefaultQuoteStyle(): QuoteStyle {
        return when (sharedPreferences.getString(QUOTE_STYLE_KEY, "DefaultTheme")) {
            "CodeSnippetTheme" -> QuoteStyle.CodeSnippetTheme
            "SpotifyTheme" -> QuoteStyle.SpotifyTheme
            else -> QuoteStyle.DefaultTheme
        }
    }

    companion object {
        private const val QUOTE_STYLE_KEY = "quote_style_key"
    }
}