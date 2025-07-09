package com.example.doctorapp.MODEL

data class ReviewModel(
    val did: String,
    val dep_id: String,
    val uName: String,
    val review: String,
    val rating: String,
    val appointment_id: String,
    val date: String
)
