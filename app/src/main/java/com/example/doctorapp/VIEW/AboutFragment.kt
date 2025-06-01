package com.example.doctorapp.VIEW

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.doctorapp.R
import com.example.doctorapp.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    lateinit var aboutFragmentBinding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        aboutFragmentBinding = FragmentAboutBinding.inflate(layoutInflater)

        val about = requireArguments().getString("about")
        aboutFragmentBinding.about.text = about?.replace("\"","")
        Log.d("@about", "onCreateView: $about")
        return aboutFragmentBinding.root
    }
}