package com.example.doctorapp.Authentication

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.doctorapp.UTIL.Static.Companion.SUPABASEKEY
import com.example.doctorapp.UTIL.Static.Companion.SUPABASEURL
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.createSupabaseClient
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import java.util.concurrent.TimeUnit

class PhoneNumberVerification {


    companion object{

        val phoneNumberVerification by lazy {
            PhoneNumberVerification()
        }

        private val supabase = createSupabaseClient(
            supabaseUrl = SUPABASEURL,
            supabaseKey = SUPABASEKEY
        ){
            install(Auth)
        }
    }
    suspend fun getCode(phoneNumber:String?){
        val result = supabase.auth.signUpWith(OTP){
            phone = phoneNumber
        }
        Log.d("@session", "getCode: ${supabase.auth.currentSessionOrNull()!!.user}")
    }
    suspend fun verifyOtp(phoneNumber:String?, otp:String?){
        Log.d("@session", "getCode: ${supabase.auth.currentSessionOrNull()!!.user}")
        val result = supabase.auth.verifyPhoneOtp(
            type = OtpType.Phone.SMS,
            phone = phoneNumber!!,
            token = otp!!
        )
        Log.d("@session", "getCode: ${supabase.auth.currentSessionOrNull()!!.user}")
    }







}