package ru.zenquotes.quotes.helpers

interface HomeEffect {
    data class ShowToast(val message: String) : HomeEffect
}