package dev.ridill.mym.settings.presentation.sign_in

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.ridill.mym.R
import dev.ridill.mym.core.util.logD
import dev.ridill.mym.core.util.tryOrNull
import kotlinx.coroutines.tasks.await

class GoogleAuthClient(
    private val context: Context
) {
    private val auth = Firebase.auth
    private val signInClient = Identity.getSignInClient(context)

    suspend fun signIn(): IntentSender? {
        val result = tryOrNull {
            signInClient.beginSignIn(buildSignInRequest()).await()
        }

        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignedInUserData? {
        val credential = signInClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

        return tryOrNull {
            val result = auth.signInWithCredential(googleCredential).await()
            val data = result.user
            logD { "User Name - ${data?.displayName}" }
            logD { "User Email - ${data?.email}" }
            SignedInUserData(
                name = data?.displayName.orEmpty(),
                email = data?.email.orEmpty()
            )
        }
    }

    fun getSignedInUser(): SignedInUserData? = auth.currentUser?.let { data ->
        SignedInUserData(
            name = data.displayName.orEmpty(),
            email = data.email.orEmpty()
        )
    }

    suspend fun signOut() = tryOrNull {
        signInClient.signOut().await()
        auth.signOut()
    }

    private fun buildSignInRequest(): BeginSignInRequest = BeginSignInRequest.Builder()
        .setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.Builder()
                .setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()
}