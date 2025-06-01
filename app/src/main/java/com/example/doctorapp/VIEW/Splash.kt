package com.example.doctorapp.VIEW

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils.loadAnimation
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.databinding.ActivitySplashBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.getValue


class Splash : AppCompatActivity() {

    private lateinit var splashBinding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        loadProgress()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val animation = loadAnimation(this, R.anim.zoom_out_anim)
        val viewmodel by viewModels<DocViewModel>()
        splashBinding.appName.startAnimation(animation)
        Handler().postDelayed({
            splashBinding.appName.visibility = View.GONE
            splashBinding.appConcern.visibility = View.VISIBLE
            val animation = loadAnimation(this, R.anim.zoom_in_anim)
            splashBinding.appConcern.startAnimation(animation)
            Handler(Looper.getMainLooper()).postDelayed({
                lifecycleScope.launch {
                    Log.d("USER@", "onCreate: $firebaseUser")
                    if (firebaseUser != null) {
                        try {
                            val response = viewmodel.getUser(firebaseUser!!.uid)
                            if (response.isSuccessful) {
                                USER = response.body()!!
                                Log.d("USER@", "onCreate: $USER")
                                if (USER != null) {
                                    if (USER!!.isPhoneVerified) {
                                        if (USER!!.isDetailsFilled) {
                                            startActivity(Intent(this@Splash, Home::class.java))
                                        } else {
                                            startActivity(Intent(this@Splash, UserInfo::class.java))
                                        }
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@Splash,
                                                PhoneVerification::class.java
                                            )
                                        )
                                    }
                                } else {
                                    startActivity(Intent(this@Splash, LoginActivity::class.java))
                                }
                                finish()
                            }else{
                                val alert = MaterialAlertDialogBuilder(this@Splash)
                                alert.also {
                                    it.setTitle("Cautious")
                                    it.setMessage("We are having problem with our server, please try again later")
                                    it.setPositiveButton("Exit") {dialog,_ ->
                                        dialog.dismiss()
                                        finish()
                                    }
                                    it.setCancelable(false)
                                    it.show()
                                }
                            }
                        }catch (_: Exception){
                            FirebaseAuth.getInstance().signOut()
                            val alert = MaterialAlertDialogBuilder(this@Splash)
                            alert.also {
                                it.setTitle("Cautious")
                                it.setMessage("We need to restart the app")
                                it.setPositiveButton("restart") { dialog, _ ->
                                    dialog.dismiss()
                                    finish()
                                }
                                it.show()
                            }
                        }
                    }else{
                        startActivity(Intent(this@Splash, LoginActivity::class.java))
                        finish()
                    }
                }
            },4500)
        },500)
    }

    private fun loadProgress() {
        splashBinding.progress.max = 100
        Handler().postDelayed({
            splashBinding.progress.progress += 25
            loadProgress()
        },500)
    }
}