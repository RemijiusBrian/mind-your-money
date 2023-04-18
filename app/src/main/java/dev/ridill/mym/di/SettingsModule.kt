package dev.ridill.mym.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ridill.mym.settings.data.remote.GDriveApi
import dev.ridill.mym.settings.domain.back_up.BackupManager
import dev.ridill.mym.settings.domain.back_up.BackupNotificationManager
import dev.ridill.mym.settings.domain.back_up.BackupService
import dev.ridill.mym.settings.presentation.sign_in.GoogleAuthClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    fun provideGoogleAuthClient(
        @ApplicationContext context: Context
    ): GoogleAuthClient = GoogleAuthClient(context)

    @Provides
    fun provideGDriveApi(): GDriveApi = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GDriveApi::class.java)

    @Provides
    fun provideBackupService(
        @ApplicationContext context: Context
    ): BackupService = BackupService(context)

    @Provides
    fun provideBackupManager(
        @ApplicationContext context: Context
    ): BackupManager = BackupManager(context)

    @Provides
    fun provideBackupNotificationManager(
        @ApplicationContext context: Context
    ): BackupNotificationManager = BackupNotificationManager(context)
}