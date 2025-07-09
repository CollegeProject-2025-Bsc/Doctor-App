package com.example.doctorapp.VIEW_MODEL


import androidx.lifecycle.ViewModel
import com.example.doctorapp.API.ApiInstance
import com.example.doctorapp.MODEL.AppointmentModel
import com.example.doctorapp.MODEL.AppointmentRequestModel
import com.example.doctorapp.MODEL.AppointmentResult
import com.example.doctorapp.MODEL.BannerModel
import com.example.doctorapp.MODEL.CreateUserResult
import com.example.doctorapp.MODEL.DepartmentModel
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.MODEL.OtpModel
import com.example.doctorapp.MODEL.PhoneModel
import com.example.doctorapp.MODEL.ReviewModel
import com.example.doctorapp.MODEL.ReviewResultModel
import com.example.doctorapp.MODEL.UserModel
import com.google.android.gms.common.api.Api
import retrofit2.Response
import java.io.Serializable

class DocViewModel:ViewModel(), Serializable {

    suspend fun getCategory(): Response<List<DepartmentModel>> = ApiInstance.API.getDepartment()
    suspend fun getPopularDoctors(): Response<List<DoctorModel>> = ApiInstance.API.getPopularDoctors()
    suspend fun getOtp(phoneNumber: PhoneModel): Response<OtpModel> = ApiInstance.API.getOtp(phoneNumber)
    suspend fun createUser(uid: String): Response<CreateUserResult>  = ApiInstance.API.createUSer(uid)
    suspend fun getUser(uid: String): Response<UserModel>  = ApiInstance.API.getUser(uid)
    suspend fun getBanner(): Response<List<BannerModel>> = ApiInstance.API.getBanners()
    suspend fun updateUser(user: UserModel): Response<CreateUserResult>  = ApiInstance.API.updateUser(user = user)
    suspend fun getDepartmentDoctor(id:String): Response<List<DoctorModel>> = ApiInstance.API.getDepartmentDoctor(id)
    suspend fun bookAppointment(appointmentRequestModel: AppointmentRequestModel): Response<AppointmentResult> = ApiInstance.API.bookAppointment(appointmentRequestModel)
    suspend fun getAppointmentFromUser(uid:String):Response<List<AppointmentModel>> = ApiInstance.API.getAppointmentFromUser(uid)
    suspend fun deleteAppointment(id:String, email:String) = ApiInstance.API.deleteAppointment(id,email)

    suspend fun addFavDoc(uid:String, did:String, specialization:String) = ApiInstance.API.addFavDoctor(uid,did, specialization)
    suspend fun getSearchResult(keyword:String): Response<List<DoctorModel>> = ApiInstance.API.getSearchResult(keyword)

    suspend fun getFavDoctor(uid:String): Response<List<DoctorModel>> = ApiInstance.API.getFavDoc(uid)
    suspend fun deleteFavDoc(uid:String,did:String) = ApiInstance.API.deleteFavDoctor(uid,did)

    suspend fun addReview(review: ReviewModel) = ApiInstance.API.addReview(review)

    suspend fun getReview(did:String,dep_id: String): Response<List<ReviewResultModel>> = ApiInstance.API.getReview(dep_id,did)

}