package com.example.doctorapp.VIEW

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.doctorapp.UTIL.Static.Companion.USER
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.doctorapp.Authentication.LoginAuth
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.mToast
import com.example.doctorapp.databinding.ActivityUserProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class UserProfile : AppCompatActivity() {

    lateinit var userProfileBinding: ActivityUserProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        userProfileBinding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(userProfileBinding.root)

        try{
            val path = USER!!.uProfilePic
            val file = File(path)
            if (file.exists()) {
                Glide.with(this)
                    .load(file)
                    .centerCrop()
                    .error(R.drawable.male_avatar)
                    .into(userProfileBinding.profilePic)
            } else {
                Log.e("ImageLoad", "File not found: $path")
            }

        }catch (e: Exception){
            Snackbar.make(userProfileBinding.root,e.message.toString(), Snackbar.LENGTH_LONG).show()
        }


        userProfileBinding.back.setOnClickListener {
            onBackPressed()
        }


        userProfileBinding.privacy.setOnClickListener {
            startActivity(Intent(this, Privacy::class.java))
        }

        userProfileBinding.uName.text = USER!!.uName
        userProfileBinding.logOut.setOnClickListener {
            val alert = MaterialAlertDialogBuilder(this).also {
                it.setTitle("Log Out!")
                it.setMessage("Do you want to log out? ")
                it.setPositiveButton("log out"){
                    dialog, which ->
                    Firebase.auth.signOut()
                    dialog.dismiss()
                    finishAffinity()
                }
                it.setNegativeButton("no"){
                    dialog, which ->
                        dialog.dismiss()
                }
                it.setCancelable(false)
            }
            alert.show()

        }

        userProfileBinding.myProfile.setOnClickListener {
            startActivity(Intent(this, UserProfileEdit::class.java))
        }
        userProfileBinding.fav.setOnClickListener {
            onBackPressed()
        }

        userProfileBinding.cPassword.setOnClickListener {
            val user = Firebase.auth.currentUser

            val signedInWithGoogle = user?.providerData?.any { it.providerId == "google.com" } == true
            val signedInWithFacebook = user?.providerData?.any { it.providerId == "facebook.com" } == true

            if (signedInWithGoogle) {
                Snackbar.make(userProfileBinding.root,"Can't update password you are currently logged in with google",
                    Snackbar.LENGTH_LONG).show()
            } else if(signedInWithFacebook) {
                Snackbar.make(userProfileBinding.root,"Can't update password you are currently logged in with facebook",
                    Snackbar.LENGTH_LONG).show()
            }else{
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
                    if (email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.lowercase().equals(USER!!.uEmail)){
                        lifecycleScope.launch {
                            val result = LoginAuth.loginAuth.resetPassword(email)
                            if (result.successful != null){
                                bottomSheet.dismiss()
                                Snackbar.make(userProfileBinding.root,"Password reset mail is sent to your $email",Snackbar.LENGTH_LONG).show()
                                Firebase.auth.signOut()
                                startActivity(Intent(this@UserProfile, Splash::class.java))
                                finish()
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
                        mToast.Companion.showToast(this@UserProfile,"Enter a valid email")
                    }
                }
                bottomSheet.show()
            }
        }




    }


    fun swapFrame(frame: androidx.fragment.app.Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame,frame)
        fragmentTransaction.commit()
    }
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.act_zoom_in, R.anim.act_zoom_out)
    }
}