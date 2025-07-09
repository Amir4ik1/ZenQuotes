package ru.zenquotes.common.utils

sealed class Screen(
    val route: String,
    val needBottomNav: Boolean
) {
    data object Home : Screen("Домашний", true)
    data object Fav : Screen("Любимые цитаты", true)
    data object Splash : Screen("Сплеш", false)
    data object Share : Screen("Поделиться", false)

    companion object {
        val values: List<Screen> = listOf(Home, Fav, Splash, Share)
    }

}