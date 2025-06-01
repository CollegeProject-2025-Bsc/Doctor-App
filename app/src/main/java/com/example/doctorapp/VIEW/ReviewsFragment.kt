package com.example.doctorapp.VIEW

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.doctorapp.R
import com.example.doctorapp.databinding.FragmentReviewsBinding


class ReviewsFragment : Fragment() {
    lateinit var reviewsFragmentBinding: FragmentReviewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reviewsFragmentBinding = FragmentReviewsBinding.inflate(layoutInflater)





        return reviewsFragmentBinding.root
    }
}