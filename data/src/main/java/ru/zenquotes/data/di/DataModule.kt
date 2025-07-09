package ru.zenquotes.data.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.zenquotes.common.utils.Constants
import ru.zenquotes.data.local.DefaultQuoteStylePreferencesImpl
import ru.zenquotes.data.local.QuoteDataBase
import ru.zenquotes.data.remote.QuoteApi
import ru.zenquotes.data.repository.FavoriteQuoteRepositoryImpl
import ru.zenquotes.data.repository.QuoteRepositoryImpl
import ru.zenquotes.domain.repositories.DefaultQuoteStylePreferences
import ru.zenquotes.domain.repositories.FavoriteQuoteRepository
import ru.zenquotes.domain.repositories.QuoteRepository
import ru.zenquotes.domain.usecases.favorite_screen.FavoriteLikedQuote
import ru.zenquotes.domain.usecases.favorite_screen.FavoriteQuoteUseCase
import ru.zenquotes.domain.usecases.favorite_screen.GetFavoriteQuote
import ru.zenquotes.domain.usecases.home_screen.GetQuote
import ru.zenquotes.domain.usecases.home_screen.LikedQuote
import ru.zenquotes.domain.usecases.home_screen.QuoteUseCase
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

//    @Singleton
//    @Provides
//    fun provideIsDebug(): Boolean = BuildConfig.DEBUG

    @Singleton
    @Provides
    fun providesQuoteDataBase(application: Application): QuoteDataBase =
        Room.databaseBuilder(application, QuoteDataBase::class.java, "quote_db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Singleton
    @Provides
    fun providesOkhttpClient(@ApplicationContext context: Context): OkHttpClient {
        val cacheSize = 8 * 1024 * 1024 // 8 MB
        return OkHttpClient
            .Builder()
            .cache(Cache(context.cacheDir, cacheSize.toLong()))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = /*if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else*/
                        HttpLoggingInterceptor.Level.NONE
                }
            )
            .connectTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun providesQuotesApi(okHttpClient: OkHttpClient): QuoteApi = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .build()
        .create(QuoteApi::class.java)

    @Singleton
    @Provides
    fun providesQuoteRepository(api: QuoteApi, db: QuoteDataBase): QuoteRepository =
        QuoteRepositoryImpl(api, db)

    @Singleton
    @Provides
    fun providesFavoriteQuoteRepository(db: QuoteDataBase): FavoriteQuoteRepository =
        FavoriteQuoteRepositoryImpl(db)

    @Singleton
    @Provides
    fun providesQuoteUseCase(getQuote: GetQuote, likedQuote: LikedQuote): QuoteUseCase =
        QuoteUseCase(
            getQuote = getQuote,
            likedQuote = likedQuote
        )

    @Singleton
    @Provides
    fun providesFavoriteQuoteUseCase(
        getFavoriteQuote: GetFavoriteQuote,
        favoriteLikedQuote: FavoriteLikedQuote
    ): FavoriteQuoteUseCase = FavoriteQuoteUseCase(
        getFavoriteQuote = getFavoriteQuote,
        favLikedQuote = favoriteLikedQuote
    )

    @Singleton
    @Provides
    fun providesSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = context.getSharedPreferences(
        Constants.SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    @Singleton
    @Provides
    fun providesDefaultQuoteStylePreferences(sharedPreferences: SharedPreferences): DefaultQuoteStylePreferences =
        DefaultQuoteStylePreferencesImpl(sharedPreferences)

}