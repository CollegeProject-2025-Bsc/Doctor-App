package com.example.doctorapp.VIEW

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.doctorapp.databinding.ActivitySplash2Binding
import com.example.doctorapp.databinding.ActivitySplashBinding

class Splash2 : AppCompatActivity() {
    private lateinit var splash2Binding: ActivitySplash2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splash2Binding = ActivitySplash2Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(splash2Binding.root)

        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.setSystemUiVisibility(uiOptions)

        splash2Binding.btnNext.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}