package ru.zenquotes.data.remote

import retrofit2.http.GET

interface QuoteApi {

    @GET("quotes")
    suspend fun getQuotesList(): QuotesDTO

    @GET("today")
    suspend fun getQuoteOfTheDay(): QuotesDTO
}