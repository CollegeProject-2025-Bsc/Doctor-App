package com.example.doctorapp.VIEW

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.doctorapp.R
import com.example.doctorapp.databinding.FragmentLoadingErrorBinding

class LoadingError(val link: String) : Fragment() {
    lateinit var loadingErrorBinding: FragmentLoadingErrorBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loadingErrorBinding = FragmentLoadingErrorBinding.inflate(layoutInflater)

        loadingErrorBinding.ivLottie.setAnimationFromUrl(link)

        return loadingErrorBinding.root
    }
}