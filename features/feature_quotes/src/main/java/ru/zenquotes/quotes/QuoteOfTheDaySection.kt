package ru.zenquotes.quotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import money.vivid.elmslie.core.store.ElmStore
import ru.zenquotes.quotes.helpers.HomeCommand
import ru.zenquotes.quotes.helpers.HomeEffect
import ru.zenquotes.quotes.helpers.HomeEvents
import ru.zenquotes.quotes.helpers.HomeState
import ru.zenquotes.theme.theme.poppinsFont

@Composable
fun QuoteOfTheDaySection(
    homeStore: ElmStore<HomeEvents, HomeState, HomeEffect, HomeCommand>,
) {
    val state by homeStore.states.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Transparent)
    ) {

        Column(modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()) {
            Text(
                text = "Цитата дня",
                fontFamily = poppinsFont,
                fontSize = 32.sp,
                modifier = Modifier.padding(start = 16.dp),
                color = Color.White
            )

            Text(
                text = state.quote?.quote ?: "",
                fontFamily = poppinsFont,
                fontWeight = FontWeight.Thin,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
                color = Color.White
            )
        }
    }
}