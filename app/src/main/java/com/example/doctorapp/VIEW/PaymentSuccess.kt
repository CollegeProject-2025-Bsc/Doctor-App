package com.example.doctorapp.VIEW

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.doctorapp.MODEL.AppointmentRequestModel
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.PAYMENT
import com.example.doctorapp.UTIL.Static.Companion.PAYMENT_SUCCESSFUL
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.databinding.ActivityPaymentSuccessBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentSuccess : AppCompatActivity() {
    lateinit var paymentSuccessBinding: ActivityPaymentSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        paymentSuccessBinding = ActivityPaymentSuccessBinding.inflate(layoutInflater)
        setContentView(paymentSuccessBinding.root)

        val appointment = intent.getSerializableExtra("appointment") as AppointmentRequestModel
        val doctor = intent.getSerializableExtra("doctor") as DoctorModel
        paymentSuccessBinding.payment.setAnimationFromUrl(PAYMENT)

        if (appointment.payment_mode.lowercase() != "offline"){
            paymentSuccessBinding.amount.text = "Successfully Paid ₹ ${appointment.fee}"
        }else{
            paymentSuccessBinding.amount.text = ""
        }
        lifecycleScope.launch {
            delay(5000)
            paymentSuccessBinding.view.visibility = View.VISIBLE
            paymentSuccessBinding.payment.visibility = View.GONE
            paymentSuccessBinding.done.visibility = View.VISIBLE
            delay(500)
            paymentSuccessBinding.ivLottie.setAnimationFromUrl(PAYMENT_SUCCESSFUL)
        }
        paymentSuccessBinding.date.text = appointment.appointment_date.uppercase()
        paymentSuccessBinding.time.text = appointment.appointment_slot.uppercase()
        paymentSuccessBinding.paymentStatus.text = appointment.payment_status.uppercase()
        paymentSuccessBinding.paymentMethod.text = appointment.payment_mode.uppercase()
        paymentSuccessBinding.fee.text = "₹ ${appointment.fee}"
        paymentSuccessBinding.uname.text = USER!!.uName.uppercase()
        paymentSuccessBinding.dname.text = doctor.dname.uppercase()

        paymentSuccessBinding.done.setOnClickListener {
            finish()
        }

    }
}