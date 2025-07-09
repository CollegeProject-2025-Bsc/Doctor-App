package com.example.doctorapp.MODEL

import java.io.Serializable

data class DoctorModel(
    val about: String,
    val caddress: String,
    val cemail: String,
    val clatitude: Double,
    val clongitude: Double,
    val cphone: String,
    val degree: String,
    val did: String,
    val dname: String,
    val experience: Int,
    val fee: Int,
    val profile_pic: String,
    val rating: Double,
    val schedule: Map<String, List<TimeSlot>>,
    val slot: Int,
    val specialization: String,
    val dep_id:String
): Serializable

data class TimeSlot (
    val time: String,
    val duration:Int,
    val hour: String,
): Serializable
