package ru.zenquotes.quotes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.aghajari.compose.lazyswipecards.LazySwipeCards
import money.vivid.elmslie.core.store.ElmStore
import ru.zenquotes.common.utils.Screen
import ru.zenquotes.domain.models.Quote
import ru.zenquotes.quotes.helpers.HomeCommand
import ru.zenquotes.quotes.helpers.HomeEffect
import ru.zenquotes.quotes.helpers.HomeEvents
import ru.zenquotes.quotes.helpers.HomeState
import ru.zenquotes.theme.theme.CustomBlack
import ru.zenquotes.theme.theme.CustomGray
import ru.zenquotes.theme.theme.classicFont

@Composable
fun QuoteItem(
    data: Quote,
    store: ElmStore<HomeEvents, HomeState, HomeEffect, HomeCommand>,
    navHost: NavHostController
) {
    val gradient = Brush.radialGradient(
        0.0f to CustomBlack,
        1.0f to CustomGray,
        radius = 1000.0f,
        tileMode = TileMode.Repeated
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.format_quote_40dp),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp)
                .size(40.dp)
        )

        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Transparent)
                .align(Alignment.Center)
        ) {
            Text(
                text = data.quote,
                fontFamily = classicFont,
                fontWeight = FontWeight.Normal,
                fontSize = 19.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                color = White,
                style = TextStyle(
                    lineHeight = 40.sp
                )
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = data.author,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomEnd)
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {


            if (data.liked) {
                AsyncImage(
                    model = R.drawable.dislike_40dp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            store.accept(HomeEvents.LikeQuote(data))
                        }
                )
            } else {
                AsyncImage(
                    model = R.drawable.like_40dp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            store.accept(HomeEvents.LikeQuote(data))
                        }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            AsyncImage(
                model = R.drawable.send_40dp,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("Share")
                    .clickable
                    {
                        navHost.currentBackStackEntry?.savedStateHandle?.set("quote", data)
                        navHost.navigate(Screen.Share.route)
                    })
        }
    }
}


@Composable
fun QuoteItemListSection(
    store: ElmStore<HomeEvents, HomeState, HomeEffect, HomeCommand>,
    navHost: NavHostController,
) {
    val state by store.states.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = White)
        }
    } else if (state.error.isNotEmpty()) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize()
                .background(color = Color.Transparent), contentAlignment = Alignment.Center
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = state.error,
                    color = White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { store.accept(HomeEvents.Retry) },
                    colors = ButtonDefaults.buttonColors(White)
                ) {
                    Text(
                        "Refresh",
                        color = Color.Black, fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

    } else {
        LazySwipeCards(
            cardColor = Color.Transparent,
            cardShadowElevation = 0.dp,
            translateSize = 8.dp,
            swipeThreshold = 0.3f
        ) {
            items(state.dataList) {
                QuoteItem(it, store, navHost)
            }

            onSwiped { item, _ ->
                state.dataList.add(item as Quote)
            }

        }
    }
}