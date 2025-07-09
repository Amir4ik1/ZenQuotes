package ru.graduate.zenquotes.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.zenquotes.common.utils.Screen
import ru.zenquotes.favorites.FavoritesScreen
import ru.zenquotes.feature_intro.SplashScreen
import ru.zenquotes.feature_share.ShareScreen
import ru.zenquotes.quotes.HomeScreen

@Composable
fun AppNavigation(navHost: NavHostController, paddingValues: PaddingValues){

    NavHost(navController = navHost, startDestination = Screen.Splash.route){
        composable(Screen.Splash.route){ SplashScreen(navHost) }
        composable(Screen.Home.route){ HomeScreen(paddingValues = paddingValues, navHost = navHost) }
        composable(Screen.Fav.route){ FavoritesScreen(paddingValues = paddingValues, navHost = navHost) }
        composable(Screen.Share.route) { ShareScreen(paddingValues = paddingValues, navHost = navHost) }
    }

}