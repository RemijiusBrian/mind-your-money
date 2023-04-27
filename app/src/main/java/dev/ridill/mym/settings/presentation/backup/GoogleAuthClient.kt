package dev.ridill.mym.settings.presentation.backup

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dev.ridill.mym.BuildConfig
import dev.ridill.mym.core.util.tryOrNull
import dev.ridill.mym.settings.domain.back_up.GDriveService
import dev.ridill.mym.settings.domain.model.SignedInUserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GoogleAuthClient(
    private val context: Context
) {
    private val signInClient = GoogleSignIn.getClient(context, buildSignInOptions())

    suspend fun getSignInIntent(): Intent {
        signInClient.signOut().await()
        return signInClient.signInIntent
    }

    suspend fun signInWithResult(
        result: ActivityResult
    ): SignedInUserData? = withContext(Dispatchers.IO) {
        tryOrNull {
            val intent = result.data ?: throw SignInFailedThrowable()
            val account = GoogleSignIn.getSignedInAccountFromIntent(intent).await()
                ?: throw SignInFailedThrowable()

            val data = SignedInUserData(
                name = account.displayName.orEmpty(),
                email = account.email.orEmpty()
            )

            data
        }
    }

    fun getSignedInUser(): SignedInUserData? = GoogleSignIn
        .getLastSignedInAccount(context)?.let { data ->
            SignedInUserData(
                name = data.displayName.orEmpty(),
                email = data.email.orEmpty()
            )
        }

    fun getSignedInAccount(): GoogleSignInAccount? = GoogleSignIn
        .getLastSignedInAccount(context)

    private fun buildSignInOptions(): GoogleSignInOptions = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        .requestScopes(GDriveService.Scope)
        .build()
}

class SignInFailedThrowable : Throwable("Sign In Failed")