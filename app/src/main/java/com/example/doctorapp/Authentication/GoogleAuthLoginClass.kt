package com.example.doctorapp.Authentication

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import com.example.doctorapp.MODEL.GLoginError
import com.example.doctorapp.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthLoginClass(
    val context: Context,
    val oneTapClient: SignInClient,
) {

    private val auth = FirebaseAuth.getInstance()


    suspend fun signIn(): IntentSender?{
        Log.d("@TAG", "signIn: inside g signin interface")
        val result = try{
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Log.d("@TAG", e.message.toString())
            null
        }
        return result?.pendingIntent?.intentSender
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }

    suspend fun getSignInResultFromIntent(intent: Intent): GLoginError {
        val credentials = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credentials.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            auth.signInWithCredential(googleCredentials).await()
            GLoginError(null)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("@TAG", "signIn: inside g signin interface")
            GLoginError(e.message.toString())
        }
    }
    suspend fun signOut() {
        try {
            oneTapClient.signOut()
            auth.signOut()
        }catch (e:Exception){
            if(e is CancellationException) throw e
        }
    }
}
