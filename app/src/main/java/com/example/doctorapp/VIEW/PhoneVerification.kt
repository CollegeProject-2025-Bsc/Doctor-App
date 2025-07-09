package com.example.doctorapp.VIEW


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.doctorapp.MODEL.PhoneModel
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.UTIL.mToast
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.databinding.ActivityPhoneVerificationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.getValue

class PhoneVerification : AppCompatActivity() {
    private lateinit var phoneVerificationBinding: ActivityPhoneVerificationBinding
    private lateinit var auth: FirebaseAuth
    lateinit var otp:String
    var time = 60
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        phoneVerificationBinding = ActivityPhoneVerificationBinding.inflate(layoutInflater)
        setContentView(phoneVerificationBinding.root)
        auth = FirebaseAuth.getInstance()


        phoneVerificationBinding.sendOtp.setOnClickListener {
            phoneVerificationBinding.ccp.registerCarrierNumberEditText(phoneVerificationBinding.number)
            if (phoneVerificationBinding.ccp.isValidFullNumber) {
                phoneVerificationBinding.sendOtp.visibility = View.GONE
                phoneVerificationBinding.progressP.visibility = View.VISIBLE
                lifecycleScope.launch {
                    try {
                        val viewmodel by viewModels<DocViewModel>()
                        val resultOtp = viewmodel.getOtp(PhoneModel(phoneVerificationBinding.ccp.fullNumberWithPlus))
                        otp = resultOtp.body()!!.otp
                        Log.d("@otp",otp)
                        if (resultOtp.isSuccessful){
                            phoneVerificationBinding.otp.visibility = View.VISIBLE
                            phoneVerificationBinding.phone.visibility = View.GONE
                            phoneVerificationBinding.timer.text = "01:00"
                            startCountDown()
                        }else{
                            phoneVerificationBinding.progressP.visibility = View.GONE
                            phoneVerificationBinding.sendOtp.visibility = View.VISIBLE
                            Snackbar.make(phoneVerificationBinding.root,resultOtp.message(),Snackbar.LENGTH_LONG).show()
                        }

                    }catch (e:Exception){
                        phoneVerificationBinding.progressP.visibility = View.GONE
                        phoneVerificationBinding.sendOtp.visibility = View.VISIBLE
                        Snackbar.make(phoneVerificationBinding.root,e.toString(),Snackbar.LENGTH_LONG).show()
                        Log.d("@phoneVerification", e.message.toString())
                    }
                }
            }else{
                phoneVerificationBinding.progressP.visibility = View.GONE
                phoneVerificationBinding.sendOtp.visibility = View.VISIBLE
                phoneVerificationBinding.number.error = "Invalid Number"
            }
        }
        phoneVerificationBinding.back.setOnClickListener {
            finish()
        }
        phoneVerificationBinding.getOtpAgain.setOnClickListener {
            phoneVerificationBinding.resendText.visibility = View.VISIBLE
            phoneVerificationBinding.timer.text = "01:00"
            time = 60
            startCountDown()
            getOtp()
        }
        phoneVerificationBinding.optSubmit.setOnClickListener {
            phoneVerificationBinding.progressO.visibility = View.VISIBLE
            phoneVerificationBinding.optSubmit.visibility = View.GONE
            lifecycleScope.launch {
                try {
                    if(otp == phoneVerificationBinding.pinview.text.toString()){
                        phoneVerificationBinding.pinview.setLineColor(
                            ResourcesCompat.getColor(getResources(), R.color.ui_green, getTheme()))
                        Handler().postDelayed({
                            goToUserForm(phoneVerificationBinding.ccp.fullNumberWithPlus)
                        },1000)
                    }else{
                        phoneVerificationBinding.pinview.setLineColor(
                            ResourcesCompat.getColor(getResources(), R.color.ui_red, getTheme()))
                        mToast.showToast(applicationContext,"Enter a valid otp")
                        phoneVerificationBinding.progressO.visibility = View.GONE
                        phoneVerificationBinding.optSubmit.visibility = View.VISIBLE
                    }


                }catch (e:Exception){
                    phoneVerificationBinding.pinview.setLineColor(
                        ResourcesCompat.getColor(getResources(), R.color.ui_red, getTheme()))
                    mToast.showToast(applicationContext,"Enter a valid otp")
                    phoneVerificationBinding.progressO.visibility = View.GONE
                    phoneVerificationBinding.optSubmit.visibility = View.VISIBLE
                }
            }

        }
    }

    private fun startCountDown(){
        if (time>0){
            time--
            Handler().postDelayed({
                phoneVerificationBinding.timer.text = "00:$time"
                startCountDown()
            },1000)
        }else{
            phoneVerificationBinding.resendText.visibility = View.GONE
            phoneVerificationBinding.timer.text = "Resend Otp"
        }
    }

    private fun getOtp(){
        val viewmodel by viewModels<DocViewModel>()
        lifecycleScope.launch {
            val resultOtp = viewmodel.getOtp(PhoneModel(phoneVerificationBinding.ccp.fullNumberWithPlus))
            otp = resultOtp.body()!!.otp.toString()
            Log.d("@otp",otp)
        }

    }
    private fun goToUserForm(phone: String){
        val viewmodel by viewModels<DocViewModel>()
        lifecycleScope.launch {
            USER!!.isPhoneVerified = true
            USER!!.uPhone = phone
            USER!!.uEmail = auth.currentUser!!.email.toString()
            USER!!.uProfilePic = auth.currentUser!!.photoUrl.toString()
            USER!!.uName = auth.currentUser!!.displayName.toString()
            viewmodel.updateUser(user = USER!!)
            startActivity(Intent(applicationContext, UserInfo::class.java).putExtra("phone",phoneVerificationBinding.ccp.fullNumberWithPlus))
            finish()
        }

    }

}