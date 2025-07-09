package ru.zenquotes.quotes.store

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import money.vivid.elmslie.core.store.ElmStore
import ru.zenquotes.domain.usecases.home_screen.QuoteUseCase
import ru.zenquotes.quotes.helpers.HomeEvents
import ru.zenquotes.quotes.helpers.HomeState
import javax.inject.Inject

@HiltViewModel
class HomeStore @Inject constructor(
    private val getQuoteUseCase: QuoteUseCase
) : ViewModel() {
    val store = ElmStore(
        initialState = HomeState(),
        reducer = HomeReducer(),
        actor = HomeActor(getQuoteUseCase)
    )

    init {
        store.accept(HomeEvents.Init)
    }
}

