package com.shinjaehun.jetpacknotesmvvmkotlinv2.login

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getString
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.shinjaehun.jetpacknotesmvvmkotlinv2.BuildConfig
import com.shinjaehun.jetpacknotesmvvmkotlinv2.R
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.*
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.LoginResult
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.User
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.repository.IUserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import kotlin.coroutines.CoroutineContext

/**
 * This approach to ViewModels reduces the complexity of the View by containing specific details about widgets and
 * controls present in the View. The benefit of doing so is to make the View in to a Humble Object; reducing or
 * eliminating the need to test the View.
 *
 * The downside of this approach, is that the ViewModel is no longer re-usable across a variety of Views. In this case,
 * since this ViewModel is only used by a single View, and the application architecture will not change any time soon,
 * losing re-usability in exchange for a simpler View is not a problem.
 */

private const val TAG = "UserViewModel"

class UserViewModel(
    val repo: IUserRepository,
    uiContext: CoroutineContext
) : BaseViewModel<LoginEvent<LoginResult>>(uiContext) {

    //The actual data model is kept private to avoid unwanted tampering
    private val userState = MutableLiveData<User?>()

    //Control Logic
    internal val authAttempt = MutableLiveData<Unit>()
    internal val startAnimation = MutableLiveData<Unit>()

    //UI Binding
    internal val signInStatusText = MutableLiveData<String>()
    internal val authButtonText = MutableLiveData<String>()
    internal val satelliteDrawable = MutableLiveData<String>()

    private fun showErrorState() {
        Log.i(TAG, "showErrorState")
        signInStatusText.value = LOGIN_ERROR
        authButtonText.value = SIGN_IN
        satelliteDrawable.value = ANTENNA_EMPTY
    }

    private fun showLoadingState() {
        Log.i(TAG, "showLoadingState")
        signInStatusText.value = LOADING
        satelliteDrawable.value = ANTENNA_LOOP
        startAnimation.value = Unit // 이렇게 해서 애니메이션이 시작된다는 게 신기!
    }

    private fun showSignedInState() {
        Log.i(TAG, "showSignedInState")
        signInStatusText.value = SIGNED_IN
        authButtonText.value = SIGN_OUT
        satelliteDrawable.value = ANTENNA_FULL
    }

    private fun showSignedOutState() {
        Log.i(TAG, "showSignedOutState")
        signInStatusText.value = SIGNED_OUT
        authButtonText.value = SIGN_IN
        satelliteDrawable.value = ANTENNA_EMPTY
    }

    override fun handleEvent(event: LoginEvent<LoginResult>) {
        //Trigger loading screen first
        showLoadingState()
        when (event) {
            is LoginEvent.OnStart -> getUser()
            is LoginEvent.OnAuthButtonClick -> onAuthButtonClick()
//            is LoginEvent.OnGoogleSignInResult -> onSignInResult(event.result)
            else -> { Log.i(TAG, "얘가 출력되면 안되는디...")}
        }
    }

    private fun getUser() = launch {
        val result = repo.getCurrentUser()
        when (result) {
            is Result.Value -> {
                userState.value = result.value
                if (result.value == null) showSignedOutState()
                else showSignedInState()
            }
            is Result.Error -> showErrorState()
        }
    }

    /**
     * If user is null, tell the View to begin the authAttempt. Else, attempt to sign the user out
     */
    private fun onAuthButtonClick() {
        if (userState.value == null) authAttempt.value = Unit
        // 이게 LoginView viewModel.authAttempt.observe()의 trigger래, startSignInFlow()를 실행하게 됨
        else signOutUser()
    }


    private fun signOutUser() = launch {
        val result = repo.signOutCurrentUser()

        when (result) {
            is Result.Value -> {
                userState.value = null
                showSignedOutState()
            }
            is Result.Error -> showErrorState()
        }
    }

//    private fun onSignInResult(result: LoginResult) = launch {
//        if (result.requestCode == RC_SIGN_IN && result.userToken != null) {
//
//            val createGoogleUserResult = repo.signInGoogleUser(
//                result.userToken
//            )
//
//            //Result.Value means it was successful
//            if (createGoogleUserResult is Result.Value) getUser()
//            else showErrorState()
//        } else {
//            showErrorState()
//        }
//    }

    fun googleSignIn(context: Context) {
        val credentialManager: CredentialManager = CredentialManager.create(context)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(BuildConfig.API_KEY)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                    val createGoogleUserResult = repo.signInGoogleUser(googleIdTokenCredential.idToken)
                    if (createGoogleUserResult is Result.Value) getUser()
                    else showErrorState()
                }

            } catch (e: Exception) {
                Log.i(TAG, "$e")
                showErrorState()
            }
        }
    }
}