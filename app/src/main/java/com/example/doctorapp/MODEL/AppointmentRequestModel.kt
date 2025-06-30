package com.example.doctorapp.MODEL

import java.io.Serializable

data class AppointmentRequestModel(
    val uid: String,
    val did: String,
    val appointment_slot: String,
    val appointment_date: String,
    val day: String,
    val payment_mode: String,
    val payment_status: String,
    val payment_id: String,
    val fee :Int
): Serializable
