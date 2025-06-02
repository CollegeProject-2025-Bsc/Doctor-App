package com.example.doctorapp.MODEL

import java.io.Serializable

data class BannerModel(
    val description: String,
    val id: String,
    val link: String,
    val pic: String
): Serializable