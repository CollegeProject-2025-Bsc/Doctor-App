package com.example.doctorapp.MODEL

import java.io.Serializable


data class DoctorModel(
    val about: String,
    val caddress: String,
    val cemail: String,
    val clatitude: Float,
    val clongitude: Float,
    val cphone: String,
    val degree: String,
    val did: String,
    val dname: String,
    val experience: Int,
    val fee: Int,
    val profile_pic: String,
    val rating: Float,
    val slot: Int,
    val specialization: String
): Serializable