package com.shinjaehun.jetpacknotesmvvmkotlinv2.login

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.api.ResourceProto.resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.shinjaehun.jetpacknotesmvvmkotlinv2.R
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.ANTENNA_LOOP
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.RC_SIGN_IN
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.startWithFade
import com.shinjaehun.jetpacknotesmvvmkotlinv2.databinding.FragmentLoginBinding
import com.shinjaehun.jetpacknotesmvvmkotlinv2.login.buildlogic.LoginInjector
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.LoginResult
import com.shinjaehun.jetpacknotesmvvmkotlinv2.note.NoteActivity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID


private const val TAG = "LoginView"

class LoginView : Fragment() {

    private lateinit var viewModel: UserViewModel
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_login, container, false)
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(
            this,
            LoginInjector(requireActivity().application).provideUserViewModelFactory()
        ).get(UserViewModel::class.java)

        (binding.rootFragmentLogin.background as AnimationDrawable).startWithFade()

        setUpClickListeners()
        observeViewModel()

        viewModel.handleEvent(LoginEvent.OnStart)
    }

    private fun setUpClickListeners() {
        binding.btnAuthAttempt.setOnClickListener {
            viewModel.handleEvent(LoginEvent.OnAuthButtonClick)
        }

        binding.imbToolbarBack.setOnClickListener { startListActivity() }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            startListActivity()
        }
    }

    private fun observeViewModel() {
        viewModel.signInStatusText.observe(
            viewLifecycleOwner,
            Observer { text ->
                binding.lblLoginStatusDisplay.text = text
            }
        )

        viewModel.authButtonText.observe(
            viewLifecycleOwner,
            Observer {
                //"it" is the value of the MutableLiveData object, which is inferred to be a String automatically
                binding.btnAuthAttempt.text = it
            }
        )

        viewModel.startAnimation.observe(
            viewLifecycleOwner,
            Observer {

                // 이건 없어도 되는 거 아닐까? -> 그냥 ANTENNA_EMPTY, ANTENNA_FULL일 수 있음...
//                binding.imvAntennaAnimation.setImageResource(
//                    resources.getIdentifier(ANTENNA_LOOP, "drawable", activity?.packageName)
//                )
                binding.imvAntennaAnimation.setImageResource(
                    R.drawable.antenna_loop
                )
                (binding.imvAntennaAnimation.drawable as AnimationDrawable).start()
            }
        )

        viewModel.authAttempt.observe(
            viewLifecycleOwner,
            Observer {
//                startSignInFlow()
                viewModel.googleSignIn(requireActivity()) // 근데 이렇게 해도 되는거예요??
            }
        )

        viewModel.satelliteDrawable.observe(
            viewLifecycleOwner,
            Observer {
                // getIdentifier 대신 그냥 R.drawable로 받아서 처리하려면 어떻게 해야지?
                binding.imvAntennaAnimation.setImageResource(
                    resources.getIdentifier(it, "drawable", activity?.packageName)
                )
            }
        )
    }

    private fun startListActivity() = requireActivity().startActivity(
        Intent(activity, NoteActivity::class.java)
    )

//    private fun startSignInFlow() {
//        Log.i(TAG, "startSignInFlow()")
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.web_client_id))
//            .build()
//
//        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
//
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//        var userToken: String? = null
//
//        try {
//            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
//
//            if (account != null) userToken = account.idToken
//        } catch (exception: Exception) {
//            Log.d("LOGIN", exception.toString())
//        }
//
//        viewModel.handleEvent(
//            LoginEvent.OnGoogleSignInResult(
//                LoginResult(
//                    requestCode,
//                    userToken
//                )
//            )
//        )
//    }
}
