package ru.zenquotes.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import ru.zenquotes.common.utils.Screen
import ru.zenquotes.domain.models.Quote
import ru.zenquotes.favorites.helpers.FavoritesEvents
import ru.zenquotes.favorites.store.FavoritesStore
import ru.zenquotes.theme.theme.CustomBlack
import ru.zenquotes.theme.theme.CustomGray

@Composable
fun FavoriteItem(
    quote: Quote,
    store: FavoritesStore,
    navHost: NavHostController,
    modifier: Modifier
) {

    val gradient = Brush.radialGradient(
        0.0f to CustomBlack,
        1.0f to CustomGray,
        radius = 600.0f,
        tileMode = TileMode.Repeated
    )
    Box(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.wrapContentSize()) {
            AsyncImage(
                model = R.drawable.format_quote_40dp,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp, top = 12.dp)
                    .size(40.dp)
            )
            Text(
                text = quote.quote,
                fontFamily = ru.zenquotes.theme.theme.classicFont,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = Color.White,
                style = TextStyle(lineHeight = 40.sp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = quote.author,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Box(modifier = Modifier.wrapContentWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    AsyncImage(
                        model = R.drawable.send_40dp,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 16.dp, bottom = 12.dp)
                            .size(40.dp)
                            .clickable {
                                navHost.currentBackStackEntry?.savedStateHandle?.set("quote", quote)
                                navHost.navigate(Screen.Share.route)
                            })
                    Spacer(modifier = Modifier.width(12.dp))
                    AsyncImage(
                        model = R.drawable.dislike_40dp,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 16.dp, bottom = 12.dp)
                            .size(40.dp)
                            .clickable { store.store.accept(FavoritesEvents.DislikeQuote(quote)) })
                }
            }
        }
    }

}