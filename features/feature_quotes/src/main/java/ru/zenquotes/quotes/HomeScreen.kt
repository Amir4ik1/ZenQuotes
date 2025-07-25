package ru.zenquotes.quotes

import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import money.vivid.elmslie.core.store.ElmStore
import ru.zenquotes.quotes.helpers.HomeCommand
import ru.zenquotes.quotes.helpers.HomeEffect
import ru.zenquotes.quotes.helpers.HomeEvents
import ru.zenquotes.quotes.helpers.HomeState
import ru.zenquotes.quotes.store.HomeStore

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    navHost: NavHostController,
    store: ElmStore<HomeEvents, HomeState, HomeEffect, HomeCommand> = hiltViewModel<HomeStore>().store
) {
    val effects = remember { store.effects }
    val context = LocalContext.current

    var isVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(1000)
        isVisible = true
    }

    LaunchedEffect(effects) {
        try {
            effects.collect { effect ->
                when (effect) {
                    is HomeEffect.ShowToast -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black))
    {
        val painter = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            rememberAsyncImagePainter(R.drawable.sora_color_bg)
        } else {
            painterResource(R.drawable.sora_color_bg)
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 3000)),
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd),
        ) {

            Image(
                painter = painter,
                contentDescription = null
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(paddingValues)
        ) {
            QuoteOfTheDaySection(store)
            QuoteItemListSection(store, navHost)
        }
    }

}







