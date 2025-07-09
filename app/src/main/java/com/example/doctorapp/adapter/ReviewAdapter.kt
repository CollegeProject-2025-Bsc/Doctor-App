package com.example.doctorapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doctorapp.MODEL.ReviewResultModel
import com.example.doctorapp.R
import com.google.android.libraries.places.api.model.Review

class ReviewAdapter(private val reviews: List<ReviewResultModel>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviewerName: TextView = itemView.findViewById(R.id.reviewerName)
        val ratingBar: RatingBar = itemView.findViewById(R.id.reviewRatingBar)
        val reviewText: TextView = itemView.findViewById(R.id.reviewText)
        val reviewDate: TextView = itemView.findViewById(R.id.reviewDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_card, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.reviewerName.text = review.user
        holder.ratingBar.rating = review.rating.toFloat()
        holder.reviewText.text = review.review
        holder.reviewDate.text = review.date
    }
    override fun getItemCount(): Int = reviews.size
}