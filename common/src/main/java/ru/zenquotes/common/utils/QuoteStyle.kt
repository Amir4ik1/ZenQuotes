package ru.zenquotes.common.utils

sealed class QuoteStyle {
    data object DefaultTheme : QuoteStyle()
    data object CodeSnippetTheme : QuoteStyle()
    data object SpotifyTheme : QuoteStyle()
}