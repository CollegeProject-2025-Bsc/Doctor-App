package com.example.doctorapp.VIEW

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.JOY_LOTTIE
import com.example.doctorapp.databinding.ActivityPrivacyBinding

class Privacy : AppCompatActivity() {
    lateinit var binding: ActivityPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.ivLottie.setAnimationFromUrl(JOY_LOTTIE)

    }
}