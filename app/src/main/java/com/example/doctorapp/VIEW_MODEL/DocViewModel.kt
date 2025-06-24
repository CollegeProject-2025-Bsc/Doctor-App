package com.example.doctorapp.VIEW_MODEL

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import com.example.doctorapp.API.ApiInstance
import com.example.doctorapp.Authentication.GoogleAuthLoginClass
import com.example.doctorapp.Authentication.LoginAuth
import com.example.doctorapp.Authentication.PhoneNumberVerification.Companion.phoneNumberVerification
import com.example.doctorapp.MODEL.AppointmentRequestModel
import com.example.doctorapp.MODEL.AppointmentResult
import com.example.doctorapp.MODEL.BannerModel
import com.example.doctorapp.MODEL.CreateUserResult
import com.example.doctorapp.MODEL.DepartmentModel
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.MODEL.OtpModel
import com.example.doctorapp.MODEL.PhoneModel
import com.example.doctorapp.MODEL.UserModel
import com.example.doctorapp.MODEL.loginResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Response

class DocViewModel:ViewModel() {
    suspend fun getCode(phoneNumber: String?) = phoneNumberVerification.getCode(phoneNumber)


//    suspend fun signUpWithEmail(
//        email: String?,
//        password: String?,
//        user: String?,
//        auth: FirebaseAuth,
//        context: Context,
//        lifecycleScope: LifecycleCoroutineScope
//    ):loginResult {
//        val result = LoginAuth.loginAuth.signUpWithEmail(email, password,auth,user,context,lifecycleScope)
//        Log.d("@loginResult", result.verificationMailSent.toString())
//        return result
//    }


    suspend fun getCategory(): Response<List<DepartmentModel>> = ApiInstance.API.getDepartment()
    suspend fun getPopularDoctors(): Response<List<DoctorModel>> = ApiInstance.API.getPopularDoctors()
    suspend fun getOtp(phoneNumber: PhoneModel): Response<OtpModel> = ApiInstance.API.getOtp(phoneNumber)
    suspend fun createUser(uid: String): Response<CreateUserResult>  = ApiInstance.API.createUSer(uid)
    suspend fun getUser(uid: String): Response<UserModel>  = ApiInstance.API.getUser(uid)
    suspend fun getBanner(): Response<List<BannerModel>> = ApiInstance.API.getBanners()
    suspend fun updateUser(user: UserModel): Response<CreateUserResult>  = ApiInstance.API.updateUser(user = user)

    suspend fun getDepartmentDoctor(id:String): Response<List<DoctorModel>> = ApiInstance.API.getDepartmentDoctor(id)
    suspend fun bookAppointment(appointmentRequestModel: AppointmentRequestModel): Response<AppointmentResult> = ApiInstance.API.bookAppointment(appointmentRequestModel)





}