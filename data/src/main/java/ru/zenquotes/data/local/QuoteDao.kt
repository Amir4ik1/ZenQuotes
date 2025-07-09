package ru.zenquotes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.zenquotes.domain.models.Quote

@Dao
interface QuoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteList(quotes: List<Quote>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedQuote(quote: Quote)

    @Delete
    suspend fun deleteQuote(quote: Quote)

    @Delete
    suspend fun deleteQuoteList(quotes: List<Quote>)

    @Query(" SELECT * FROM Quote WHERE liked == 1 ORDER BY id DESC ")
    fun getAllLikedQuotes(): Flow<List<Quote>>

    @Query(" SELECT * FROM Quote ORDER BY id DESC ")
    suspend fun getAllQuotes(): List<Quote>

    @Query("DELETE FROM quote")
    suspend fun deleteAll()

    @Query(
        """ SELECT * FROM Quote
            WHERE liked = 1
               AND (
                    LOWER(quote) LIKE '%' || LOWER(:query) || '%'
                    OR LOWER(author) LIKE '%' || LOWER(:query) || '%'
                    ) """
    )
    fun searchForQuotes(query: String): Flow<List<Quote>>

}