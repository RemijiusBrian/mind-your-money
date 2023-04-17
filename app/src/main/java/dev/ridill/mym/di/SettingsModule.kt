package dev.ridill.mym.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ridill.mym.settings.presentation.sign_in.GoogleAuthClient

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    fun provideGoogleAuthClient(
        @ApplicationContext context: Context
    ): GoogleAuthClient = GoogleAuthClient(context)
}