package ru.graduate.zenquotes

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import ru.zenquotes.feature_intro.SplashScreen

/**
 * Проверка экрана входа в приложение на показ compose элементов, что они прогрузились и показались
 */
class SplashScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun splash_screen_shows_zen_quotes_text() {
        composeTestRule.setContent {
            val navHost = rememberNavController()
            SplashScreen(navHost = navHost)
        }
        composeTestRule.onNodeWithText("Zen Quotes").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SplashBg").assertExists()
    }
}