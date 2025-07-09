package ru.graduate.zenquotes.di

import androidx.activity.ComponentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import ru.zenquotes.common.callback.MainActivityCallback

@Module
@InstallIn(ActivityComponent::class)
object AppModule {

    @Provides
    fun provideMainActivityCallback(activity: ComponentActivity): MainActivityCallback {
        return activity as MainActivityCallback
    }

}