package ru.zenquotes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.zenquotes.domain.models.Quote

@Database(entities = [Quote::class], version = 3)
abstract class QuoteDataBase : RoomDatabase() {

    abstract fun getQuoteDao(): QuoteDao

}