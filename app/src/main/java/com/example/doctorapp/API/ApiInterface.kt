package com.example.doctorapp.API

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {

    @POST("/Department")
    suspend fun getDepartment():Response<List<DepartmentModel>>

    @POST("/getBanners")
    suspend fun getBanners():Response<List<BannerModel>>

    @POST("/popular_docs")
    suspend fun getPopularDoctors():Response<List<DoctorModel>>

    @POST("/getOtp")
    suspend fun getOtp(
        @Body otpModel: PhoneModel,
    ): Response<OtpModel>

    @POST("/createUser")
    suspend fun createUSer(
        @Query ("UID") uid:String,
    ):Response<CreateUserResult>

    @POST("/getUser")
    suspend fun getUser(
        @Query ("UID") uid:String,
    ):Response<UserModel>
    @POST("/updateUser")
    suspend fun updateUser(
        @Body user: UserModel
    ):Response<CreateUserResult>
    @POST("/book_appointment")
    suspend fun bookAppointment(
        @Body appointmentRequestModel: AppointmentRequestModel
    ):Response<AppointmentResult>

    @POST("/get_department_doctors")
    suspend fun getDepartmentDoctor(
        @Query ("id") id:String,
    ):Response<List<DoctorModel>>


    @POST("get_user_appointments")
    suspend fun getAppointmentFromUser(
        @Query ("uid") id:String,
    ):Response<List<AppointmentModel>>


    @POST("/delete_appointment")
    suspend fun deleteAppointment(
        @Query ("appointment_id") id:String,
        @Query ("uEmail") email:String,
    )

    @POST("/search")
    suspend fun getSearchResult(
        @Query("keyword") keyword:String
    ): Response<List<DoctorModel>>

    @POST("/addFavDoctor")
    suspend fun addFavDoctor(
        @Query("uid") uid:String,
        @Query("did") did:String,
        @Query("specialization") specialization:String,
    )

    @POST("/get_fav_doctors")
    suspend fun getFavDoc(
        @Query("uid") uid:String
    ): Response<List<DoctorModel>>

    @POST("/delete_favdoctor")
    suspend fun deleteFavDoctor(
        @Query ("uid") uid:String,
        @Query ("did") did:String
    )


    @POST("/add_review")
    suspend fun addReview(
        @Body review: ReviewModel
    )

    @POST("/retrieve_review")
    suspend fun getReview(
        @Query ("dept_id") dep_id:String,
        @Query ("did") did:String
    ): Response<List<ReviewResultModel>>

}