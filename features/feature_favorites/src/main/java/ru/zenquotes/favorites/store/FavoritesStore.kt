package ru.zenquotes.favorites.store

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import money.vivid.elmslie.core.store.ElmStore
import ru.zenquotes.domain.usecases.favorite_screen.FavoriteQuoteUseCase
import ru.zenquotes.favorites.helpers.FavoritesEvents
import ru.zenquotes.favorites.helpers.FavoritesState
import javax.inject.Inject

@HiltViewModel
class FavoritesStore @Inject constructor(
    private val favoriteQuoteUseCase: FavoriteQuoteUseCase
) : ViewModel() {
    val store = ElmStore(
        initialState = FavoritesState(),
        reducer = FavoritesReducer(),
        actor = FavoritesActor(favoriteQuoteUseCase)
    )

    init {
        store.accept(FavoritesEvents.Init)
    }
}

