package com.example.doctorapp.MODEL

data class UserModel(
    var uid:String = "",
    var uName: String = "",
    var uGender: String = "",
    var DOB: String = "",
    var uAddress: String = "",
    var uLat: Float = 0.0f,
    var uLong: Float = 0.0f,
    var uEmail: String = "",
    var uPhone: String = "",
    var uProfilePic: String = "",
    var isPhoneVerified: Boolean = false,
    var isDetailsFilled: Boolean = false
)