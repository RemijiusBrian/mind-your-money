package dev.ridill.mym.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ridill.mym.BuildConfig
import dev.ridill.mym.core.data.db.MYMDatabase
import dev.ridill.mym.settings.data.remote.GDriveApi
import dev.ridill.mym.settings.domain.back_up.BackupNotificationManager
import dev.ridill.mym.settings.domain.back_up.BackupService
import dev.ridill.mym.settings.domain.back_up.BackupWorkManager
import dev.ridill.mym.settings.domain.back_up.GDriveService
import dev.ridill.mym.settings.presentation.backup.GoogleAuthClient
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
        .baseUrl(BuildConfig.GOOGLE_APIS_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GDriveApi::class.java)

    @Provides
    fun provideBackupService(
        @ApplicationContext context: Context,
        gDriveService: GDriveService,
        googleAuthClient: GoogleAuthClient,
        database: MYMDatabase
    ): BackupService = BackupService(
        context = context,
        gDriveService = gDriveService,
        authClient = googleAuthClient,
        db = database
    )

    @Provides
    fun provideBackupWorkManager(
        @ApplicationContext context: Context
    ): BackupWorkManager = BackupWorkManager(context)

    @Provides
    fun provideBackupNotificationManager(
        @ApplicationContext context: Context
    ): BackupNotificationManager = BackupNotificationManager(context)

    @Provides
    fun provideGDriveService(
        @ApplicationContext context: Context,
        api: GDriveApi
    ): GDriveService = GDriveService(context, api)
}