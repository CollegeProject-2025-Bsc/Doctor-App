package com.example.doctorapp.VIEW

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doctorapp.MODEL.ReviewResultModel
import com.example.doctorapp.R
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.adapter.ReviewAdapter
import com.example.doctorapp.databinding.FragmentReviewsBinding
import kotlinx.coroutines.launch


class ReviewsFragment : Fragment() {
    lateinit var reviewsFragmentBinding: FragmentReviewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reviewsFragmentBinding = FragmentReviewsBinding.inflate(layoutInflater)
        var reviews:List<ReviewResultModel> = emptyList()
        val viewmodel = ViewModelProvider(this)[DocViewModel::class.java]

        val dep_id = requireArguments().getString("dep_id")
        val did = requireArguments().getString("did")

        lifecycleScope.launch {
            val result = viewmodel.getReview(did!!,dep_id!!)
            if (result.isSuccessful){
                reviews = result.body()!!
                reviewsFragmentBinding.reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                reviewsFragmentBinding.reviewsRecyclerView.adapter = ReviewAdapter(reviews)
            }
        }





        return reviewsFragmentBinding.root
    }
}