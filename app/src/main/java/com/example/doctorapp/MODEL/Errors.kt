package com.example.doctorapp.MODEL

import com.google.firebase.auth.FirebaseUser

class Errors {
}

data class PasswordResetModel(
    var successful: Boolean = false,
    var errorMsg: String? = null
)


data class GLoginError(
    val errorMsg:String?
)



data class FLoginError(
    val errorMsg:String?
)


data class EmaiLoginError(
    val errorMsg:String?
)