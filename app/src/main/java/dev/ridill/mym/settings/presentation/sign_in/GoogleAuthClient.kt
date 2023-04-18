package dev.ridill.mym.settings.presentation.sign_in

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dev.ridill.mym.R
import dev.ridill.mym.core.util.tryOrNull
import dev.ridill.mym.settings.domain.back_up.GDriveService
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

    suspend fun signInWithIntent(intent: Intent): SignedInUserData? = withContext(Dispatchers.IO) {
        tryOrNull {
            GoogleSignIn.getSignedInAccountFromIntent(intent).await()?.let {
                SignedInUserData(
                    name = it.displayName.orEmpty(),
                    email = it.email.orEmpty()
                )
            }
        }
    }

    fun getSignedInUser(): SignedInUserData? = GoogleSignIn
        .getLastSignedInAccount(context)?.let { data ->
            SignedInUserData(
                name = data.displayName.orEmpty(),
                email = data.email ?: data.displayName.orEmpty()
            )
        }

    fun getSignedInAccount(): GoogleSignInAccount? = GoogleSignIn
        .getLastSignedInAccount(context)

    private fun buildSignInOptions(): GoogleSignInOptions = GoogleSignInOptions.Builder()
        .requestProfile()
        .requestEmail()
        .requestId()
        .requestIdToken(context.getString(R.string.google_web_client_id))
        .requestScopes(GDriveService.driveScopes.first(), *GDriveService.driveScopes.toTypedArray())
        .build()
}

