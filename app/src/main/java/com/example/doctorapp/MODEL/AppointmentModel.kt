package com.example.doctorapp.MODEL

import java.io.Serializable

data class AppointmentModel(
    val appointment_date: String,
    val appointment_id: String,
    val appointment_slot: String,
    val day: String,
    val doctor_id: String,
    val fee: Int,
    val payment_id: String,
    val payment_mode: String,
    val payment_status: String,
    val user_id: String,
    val time:String,
    val dname:String,
    val specialization:String,
    val pic:String,
    val dep_id:String
): Serializable