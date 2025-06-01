package com.example.doctorapp.Authentication

import android.content.Context
import com.example.doctorapp.MODEL.PasswordResetModel
import com.example.doctorapp.MODEL.UserModel
import com.example.doctorapp.UTIL.mToast
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class EmailPasswordClass {
     val auth = FirebaseAuth.getInstance()

    suspend fun createUserAccount(email:String,password:String, context: Context): AuthResult? {
        val result =  try{
            auth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    if (!it.user!!.isEmailVerified) {
                        it.user!!.sendEmailVerification()
                        auth.signOut()
                        true
                    }else{
                        false
                    }
                }.addOnFailureListener {
                    false
                }.await()
        }catch (
            e: Exception
        ){
            mToast.Companion.showToast(context,e.message.toString())
            null
        }

        return result
    }

    suspend fun signOut(){
        auth.signOut()
    }


    suspend fun resetPassword(email: String): PasswordResetModel {
        return try {
            var successful = false
            var errorMsg:String? = null
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful){
                    successful = true
                }else{
                    errorMsg = it.exception?.message.toString()
                }
            }.await()
            PasswordResetModel(successful = successful, errorMsg = errorMsg)
        }catch (e: Exception){
            PasswordResetModel(successful = false, errorMsg = e.message.toString())
        }
    }


    suspend fun loginUser(email:String,password:String,context: Context): LoginModel {
        return try {
            var message: String? = null
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                message = if (it.isSuccessful) {
                            null
                        } else {
                            it.exception?.message.toString()
                        }
            }.await()
            if (message != null) {
                LoginModel(user = null, errorMsg = message)
            } else {
                LoginModel(user = auth.currentUser!!, errorMsg = null)
            }
        } catch (e: Exception) {
            LoginModel(user = null, errorMsg = e.message.toString())
        }
    }

}


data class LoginModel(
    val user: FirebaseUser?,
    val errorMsg: String?
)