package com.example.doctorapp.VIEW

import android.util.Patterns
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.doctorapp.Authentication.GoogleAuthLoginClass
import com.example.doctorapp.Authentication.LoginAuth
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.UTIL.mToast
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    val googleAuthClient by lazy{
        GoogleAuthLoginClass(this,oneTapClient = Identity.getSignInClient(applicationContext))
    }
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        val viewmodel by viewModels<DocViewModel>()




        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.setSystemUiVisibility(uiOptions)

        FacebookSdk.sdkInitialize(applicationContext = applicationContext)
        auth = FirebaseAuth.getInstance()




        //activity result alternative
        val activityResultLauncher : ActivityResultLauncher<IntentSenderRequest> =
            registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    lifecycleScope.launch {
                        val signInResult = googleAuthClient.getSignInResultFromIntent(
                            intent = result.data ?: return@launch
                        )
                        if (signInResult == null) {
                            Snackbar.make(
                                loginBinding.root,
                                signInResult.errorMsg.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {
                            Snackbar.make(
                                loginBinding.root,
                                "Login Successful",
                                Snackbar.LENGTH_LONG
                            ).show()
                            val user = auth.currentUser
                            assert(user != null)
                            login(user)
                        }
                    }
                }
            }


        //sign up set up
        loginBinding.signUp.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            bottomSheet.setContentView(R.layout.sign_up)
            bottomSheet.setCancelable(false)
            bottomSheet.setCanceledOnTouchOutside(false)

            bottomSheet.findViewById<View>(R.id.back)?.setOnClickListener {
                bottomSheet.dismiss()
            }
            bottomSheet.findViewById<View>(R.id.signUp)?.setOnClickListener {

                bottomSheet.findViewById<View>(R.id.signUp)!!.visibility = View.GONE
                bottomSheet.findViewById<View>(R.id.progress)!!.visibility = View.VISIBLE

                val email = bottomSheet.findViewById<TextView>(R.id.email)!!.text.toString()
                val password = bottomSheet.findViewById<TextView>(R.id.password)!!.text.toString()
                val reConfirmPassword = bottomSheet.findViewById<TextView>(R.id.reConfirmPassword)!!.text.toString()

                Log.d("@login", "here")
                val check = checkEmailPassword(email,password,reConfirmPassword)

                if (check){
                    lifecycleScope.launch {
                        val result = LoginAuth.loginAuth.createUserAccount(email,password,this@LoginActivity)
                        if (result?.user != null){
                            bottomSheet.dismiss()
                            val alert = MaterialAlertDialogBuilder(this@LoginActivity)
                            alert.setTitle("Sign Up Successful")
                            alert.setMessage("Verification mail is successfully sent to your $email")
                            alert.show()
                        }else{
                            bottomSheet.dismiss()
                            Snackbar.make(loginBinding.root,"sign up failed",Snackbar.LENGTH_LONG).show()
                        }
                    }
                }else{
                    bottomSheet.findViewById<View>(R.id.signUp)!!.visibility = View.VISIBLE
                    bottomSheet.findViewById<View>(R.id.progress)!!.visibility = View.GONE
                    Toast.makeText(applicationContext,"sign up failed",Toast.LENGTH_LONG).show()
                }
            }
            bottomSheet.show()
        }


        // back btn
        loginBinding.back.setOnClickListener {
            finish()
        }



        // Initialize Google Login button
        loginBinding.gBtn.setOnClickListener {
            lifecycleScope.launch {
                val signInIntent = googleAuthClient.signIn()
                activityResultLauncher.launch(
                    IntentSenderRequest.Builder(
                        signInIntent?:return@launch
                    ).build()
                )
            }
        }



        // Initialize Facebook Login button

        loginBinding.fBtn.setOnClickListener {
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
            LoginManager.getInstance().registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Log.d("@facLogin", "facebook:onSuccess:$loginResult")
                        handleFacebookAccessToken(loginResult.accessToken)
                    }
                    override fun onCancel() {
                        Log.d("@facLogin", "facebook:onCancel")
                    }
                    override fun onError(error: FacebookException) {
                        Log.d("@facLogin", "facebook:onError", error)
                    }
                },
            )
        }



        // login btn
        loginBinding.login.setOnClickListener {
            loginBinding.progress.visibility = View.VISIBLE
            loginBinding.login.visibility = View.GONE
            val email = loginBinding.email.text.toString()
            val password = loginBinding.password.text.toString()
            lifecycleScope.launch {
                val userModel = LoginAuth.loginAuth.loginUser(email, password, this@LoginActivity)
                if (userModel.user != null) {
                    loginBinding.progress.visibility = View.GONE
                    loginBinding.login.visibility = View.VISIBLE
                    login(userModel.user)
                } else {
                    loginBinding.progress.visibility = View.GONE
                    loginBinding.login.visibility = View.VISIBLE
                    Snackbar.make(
                        loginBinding.root,
                        userModel.errorMsg.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }


        //forget password
        loginBinding.forgetPassword.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            bottomSheet.setContentView(R.layout.password_reset)
            bottomSheet.setCancelable(false)
            bottomSheet.setCanceledOnTouchOutside(false)

            bottomSheet.findViewById<View>(R.id.back)?.setOnClickListener {
                bottomSheet.dismiss()
            }
            bottomSheet.findViewById<View>(R.id.submit)?.setOnClickListener {
                bottomSheet.findViewById<View>(R.id.submit)!!.visibility = View.GONE
                bottomSheet.findViewById<View>(R.id.progress)!!.visibility = View.VISIBLE

                val email = bottomSheet.findViewById<TextView>(R.id.email)!!.text.toString()
                if (email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    lifecycleScope.launch {
                        val result = LoginAuth.loginAuth.resetPassword(email)
                        if (result.successful != null){
                            bottomSheet.dismiss()
                            Snackbar.make(loginBinding.root,"Password reset mail is sent to your $email",Snackbar.LENGTH_LONG).show()
                        }else{
                            mToast.Companion.showToast(applicationContext,
                                result.errorMsg.toString()
                            )
                            bottomSheet.findViewById<View>(R.id.submit)!!.visibility = View.VISIBLE
                            bottomSheet.findViewById<View>(R.id.progress)!!.visibility = View.GONE
                        }
                    }
                }else{
                    bottomSheet.findViewById<View>(R.id.submit)!!.visibility = View.VISIBLE
                    bottomSheet.findViewById<View>(R.id.progress)!!.visibility = View.GONE
                    mToast.Companion.showToast(this@LoginActivity,"Enter a valid email")
                }
            }
            bottomSheet.show()
        }

    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("@facLogin", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("@facLogin", "signInWithCredential:success")
                    val user = auth.currentUser
                    assert(user != null)
                    login(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("@facLogin", "signInWithCredential:failure", task.exception)
                    mToast.Companion.showToast(applicationContext,"Authentication failed.")
                }
            }
    }
    fun checkEmailPassword(email:String, password:String, reConfirmPassword:String):Boolean{

        val emailRegex = Regex("^[a-z0-9A-Z._%+-]+\\@[a-z0-9A-Z]+\\.[a-zA-Z]{3,6}$")
        val result= if (email.isNotBlank() && emailRegex.matches(email)){
                        if (password.length >=6 && password == reConfirmPassword) {
                            if (password.any{it.isUpperCase()}){
                                if (password.any{it.isLowerCase()}){
                                    if (password.any{it.isDigit()}){
                                        if(password.any{!it.isLetterOrDigit()}){
                                            true
                                        }else{
                                            mToast.Companion.showToast(applicationContext,"Password must contain special character")
                                            false
                                        }
                                    }else{
                                        mToast.Companion.showToast(applicationContext,"Password must contain numbers")
                                        false
                                    }
                                }else{
                                    mToast.Companion.showToast(applicationContext,"Password must contain small letter")
                                    false
                                }
                            }else{
                                mToast.Companion.showToast(applicationContext,"Password must contain capital letter")
                                false
                            }
                        }else{
                            mToast.Companion.showToast(applicationContext,"password is not 6 character long or mismatched with re-confirmed password ")
                            false
                        }
                    }else{
                        mToast.Companion.showToast(applicationContext,"Enter a valid email")
                        false
                    }
        return result
    }

    private fun login(user: FirebaseUser?) {
        val viewmodel by viewModels<DocViewModel>()
        lifecycleScope.launch {

            USER = viewmodel.getUser(user!!.uid).body()
            if (USER != null){
                startActivity(Intent(this@LoginActivity,Home::class.java))
            }else{
                val result = viewmodel.createUser(user!!.uid.toString())
                Log.d("@TAG", "${user!!.uid}")
                USER = viewmodel.getUser(user!!.uid.toString()).body()!!
                if (result.isSuccessful){
                    val status = result.body()
                    if (status!!.status){
                        startActivity(Intent(applicationContext, PhoneVerification::class.java))
                        Log.d("@TAG", "${status!!.status}")
                        finish()
                    }else{
                        mToast.Companion.showToast(applicationContext,"Something went wrong")
                        user.delete()
                            .addOnCompleteListener { task ->
                                mToast.Companion.showToast(applicationContext,"Try Again")
                            }

                    }
                }else{
                    mToast.Companion.showToast(applicationContext,"Something went wrong")
                    user.delete()
                        .addOnCompleteListener { task ->
                            mToast.Companion.showToast(applicationContext,"Try Again")
                        }
                }
            }

        }
    }

}