package ru.zenquotes.feature_intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import ru.zenquotes.common.utils.Screen
import ru.zenquotes.theme.theme.classicFont

@Composable
fun SplashScreen(navHost: NavHostController) {
    var isVisible by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.sora_splash_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            ShimmeringText(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "Zen Quotes",
                shimmerColor = Color.White,
                fontSize = 50.sp,
                fontFamily = classicFont,
                fontWeight = FontWeight.Medium
            )
        }
    }

    LaunchedEffect(Unit) {
        /** Время, насколько долго показывается сплеш */
        isVisible = true
        delay(2000)
        isVisible = false
        /** Небольшая задержка нужна перед дальнейшей навигацией на стартовый экран */
        delay(200)
        navHost.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) {
                inclusive = true
            }
        }
    }
}

@Composable
private fun ShimmeringText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit,
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    shimmerColor: Color,
    textStyle: TextStyle = LocalTextStyle.current,
    animationSpec: DurationBasedAnimationSpec<Float> = tween(2000, 200, LinearEasing)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ShimmeringTextTransition")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "ShimmerProgress"
    )

    val brush = remember(shimmerProgress) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val initialXOffset = -size.width
                val totalSweepDistance = size.width * 2
                val currentPosition = initialXOffset + totalSweepDistance * shimmerProgress

                return LinearGradientShader(
                    colors = listOf(Color.Transparent, shimmerColor, Color.Transparent),
                    from = Offset(currentPosition, 0f),
                    to = Offset(currentPosition + size.width, 0f)
                )
            }
        }
    }

    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        style = textStyle.copy(brush = brush)
    )
}