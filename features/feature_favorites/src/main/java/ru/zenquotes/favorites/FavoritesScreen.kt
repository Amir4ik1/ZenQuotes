package ru.zenquotes.favorites

import android.widget.Toast
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import ru.zenquotes.favorites.helpers.FavoritesEffect
import ru.zenquotes.favorites.helpers.FavoritesEvents
import ru.zenquotes.favorites.store.FavoritesStore
import ru.zenquotes.theme.theme.arialFont
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    paddingValues: PaddingValues,
    navHost: NavHostController,
    store: FavoritesStore = hiltViewModel()
) {
    val state by store.store.states.collectAsStateWithLifecycle()
    val effects = remember { store.store.effects }
    val context = LocalContext.current

    var clickedSearch by remember {
        mutableStateOf(false)
    }
    val progress by animateFloatAsState(
        targetValue = if (clickedSearch) 1f else 0f,
        label = "",
        animationSpec = tween(2000)
    )

    val pullRefreshState = rememberPullToRefreshState()

    val willRefresh by remember {
        derivedStateOf {
            pullRefreshState.distanceFraction > 1f
        }
    }

    val cardOffset by animateIntAsState(
        targetValue = when {
            state.isRefreshing -> 250
            pullRefreshState.distanceFraction in 0f..1f -> (250 * pullRefreshState.distanceFraction).roundToInt()
            pullRefreshState.distanceFraction > 1f -> (250 + ((pullRefreshState.distanceFraction - 1f) * .1f) * 100).roundToInt()
            else -> 0
        },
        label = "cardOffset"
    )

    val cardRotation by animateFloatAsState(
        targetValue = when {
            state.isRefreshing || pullRefreshState.distanceFraction > 1f -> 5f
            pullRefreshState.distanceFraction > 0f -> 5 * pullRefreshState.distanceFraction
            else -> 0f
        },
        label = "cardRotation"
    )

    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(key1 = willRefresh) {
        when {
            willRefresh -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                delay(70)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                delay(100)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }

            !state.isRefreshing && pullRefreshState.distanceFraction > 0f -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    LaunchedEffect(effects) {
        try {
            effects.collect { effect ->
                when (effect) {
                    is FavoritesEffect.ShowToast -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .pullToRefresh(
                isRefreshing = state.isRefreshing,
                onRefresh = { store.store.accept(FavoritesEvents.Refresh) },
                state = pullRefreshState
            )
            .background(color = Color.Black)
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent), contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Transparent), contentAlignment = Alignment.Center
                ) {
                    Text(state.error, color = White)
                }
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { value ->
                    store.store.accept(FavoritesEvents.OnSearchQueryChanged(value))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 15.dp,
                        start = 16.dp,
                        end = 16.dp,
                        top = 10.dp
                    )
                    .onFocusChanged { focusState ->
                        clickedSearch = focusState.isFocused
                    }
                    /** Кастомная анимация по границе поискового поля при нажатии на поиск */
                    .animatedBorder({ progress }, White, Color.Black),
                maxLines = 1,
                shape = MaterialTheme.shapes.extraLarge,
                placeholder = { Text(text = "Поиск...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Gray,
                    focusedPlaceholderColor = Gray,
                    unfocusedPlaceholderColor = Gray,
                    disabledPlaceholderColor = Color.Yellow,
                    focusedTextColor = White,
                ),
                trailingIcon = {
                    WhiteCancelIcon(onClick = {
                        clickedSearch = false
                        /** После поиска очищаем введенный в поле текст */
                        store.store.accept(FavoritesEvents.ClearSearchField)
                        /** А затем, так как не обновляем список в стейте, вызываем подгрузку из БД заново */
                        store.store.accept(FavoritesEvents.Init)
                    })
                }
            )

            if (state.dataList.isNotEmpty()) {

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(state.dataList) { index, quoteItem ->
                        FavoriteItem(
                            quoteItem, store, navHost, modifier = Modifier
                                .zIndex((state.dataList.size - index).toFloat())
                                .graphicsLayer {
                                    rotationZ = cardRotation * if (index % 2 == 0) 1 else -1
                                    translationY = (cardOffset * ((5f - (index + 1)) / 5f)).dp
                                        .roundToPx()
                                        .toFloat()
                                })

                    }
                }

            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Похоже, ничего не найдено...",
                        color = White,
                        fontFamily = arialFont,
                    )
                }
            }
        }

        CustomIndicator(state.isRefreshing, pullRefreshState)

    }
}

@Composable
fun WhiteCancelIcon(onClick: () -> Unit) {

    val focusManager = LocalFocusManager.current

    IconButton(onClick = {
        focusManager.clearFocus(true)
        onClick()
    }) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cancel",
            tint = White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomIndicator(isRefreshing: Boolean, pullRefreshState: PullToRefreshState) {

    val animatedOffset by animateDpAsState(
        targetValue = when {
            isRefreshing -> 200.dp
            pullRefreshState.distanceFraction in 0f..1f -> (pullRefreshState.distanceFraction * 200).dp
            pullRefreshState.distanceFraction > 1f -> (200 + (((pullRefreshState.distanceFraction - 1f) * .1f) * 200)).dp
            else -> 0.dp
        }, label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .offset(y = (-200).dp)
            .offset { IntOffset(0, animatedOffset.roundToPx()) }
    ) {
        WhiteBeam(pullRefreshState, isRefreshing)
        RainbowRays(pullRefreshState, isRefreshing)
        GlowingTriangle(pullRefreshState, isRefreshing)
    }

}

fun Modifier.animatedBorder(
    provideProgress: () -> Float,
    colorFocused: Color,
    colorUnfocused: Color
) = this.drawWithCache {
    val width = size.width
    val height = size.height

    val shape = CircleShape
    val outline = shape.createOutline(size, layoutDirection, this) as Outline.Rounded
    val radius = outline.roundRect.topLeftCornerRadius.x
    val diameter = 2 * radius

    val pathCw = Path()
    pathCw.moveTo(width / 2, 0f)
    pathCw.lineTo(width - radius, 0f)
    pathCw.arcTo(Rect(width - diameter, 0f, width, diameter), -90f, 90f, false)
    pathCw.lineTo(width, height - radius)
    pathCw.arcTo(Rect(width - diameter, height - diameter, width, height), 0f, 90f, false)
    pathCw.lineTo(width / 2, height)

    val pathCcw = Path()
    pathCcw.moveTo(width / 2, 0f)
    pathCcw.lineTo(radius, 0f)
    pathCcw.arcTo(Rect(0f, 0f, diameter, diameter), -90f, -90f, false)
    pathCcw.lineTo(0f, height - radius)
    pathCcw.arcTo(Rect(0f, height - diameter, diameter, height), 180f, -90f, false)
    pathCcw.lineTo(width / 2, height)

    val pmCw = PathMeasure().apply {
        setPath(pathCw, false)
    }
    val pmCcw = PathMeasure().apply {
        setPath(pathCcw, false)
    }

    fun DrawScope.drawIndicator(progress: Float, pathMeasure: PathMeasure) {
        val subPath = Path()
        pathMeasure.getSegment(0f, pathMeasure.length * EaseOut.transform(progress), subPath)
        drawPath(subPath, colorFocused, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
    }

    onDrawBehind {
        drawOutline(outline, colorUnfocused, style = Stroke(2.dp.toPx()))

        drawIndicator(provideProgress(), pmCw)
        drawIndicator(provideProgress(), pmCcw)
    }
}
