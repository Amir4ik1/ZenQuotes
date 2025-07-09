package ru.zenquotes.feature_share

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.zenquotes.common.utils.QuoteStyle
import ru.zenquotes.domain.repositories.DefaultQuoteStylePreferences
import javax.inject.Inject

@HiltViewModel
class ShareQuoteViewModel @Inject constructor(
    private val defaultQuoteStylePreferences: DefaultQuoteStylePreferences
): ViewModel() {
    fun getDefaultQuoteStyle(): QuoteStyle {
        return defaultQuoteStylePreferences.getDefaultQuoteStyle()
    }

    fun changeDefaultQuoteStyle(quoteStyle: QuoteStyle){
        defaultQuoteStylePreferences.saveDefaultQuoteStyle(quoteStyle)
    }

}