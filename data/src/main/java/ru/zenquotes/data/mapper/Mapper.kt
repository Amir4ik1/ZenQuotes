package ru.zenquotes.data.mapper

import ru.zenquotes.data.remote.QuotesDTOItem
import ru.zenquotes.domain.models.Quote

fun QuotesDTOItem.toQuote(): Quote =
    Quote(
        quote = q,
        author = a,
        liked = false
    )