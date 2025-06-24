package com.example.doctorapp.VIEW

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import com.razorpay.Checkout
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doctorapp.MODEL.AppointmentRequestModel
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.UTIL.Static.Companion.rezorpay_api_key
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.adapter.DateAdapter
import com.example.doctorapp.databinding.ActivityMakeAppointmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MakeAppointment : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    PaymentResultListener {
    lateinit var makeAppointmentBinding: ActivityMakeAppointmentBinding
    lateinit var doctor: DoctorModel
    lateinit var dates: List<DateInfo>
    lateinit var formattedDate: List<String>
    var slotSelection: Int = -1
    var daySelection = -1
    var paymentModeSelection = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        makeAppointmentBinding = ActivityMakeAppointmentBinding.inflate(layoutInflater)
        setContentView(makeAppointmentBinding.root)
        formattedDate = getNext30Days()
        dates = mapDatesToDayOfWeek(formattedDate)
        doctor = intent.getSerializableExtra("doctor") as DoctorModel

        val paymentMode = listOf(
            "Pay at the time of appointment",
            "Pay via RazorPay",
        )
        val payment = listOf("offline","online")

        Checkout.preload(applicationContext)

        makeAppointmentBinding.back.setOnClickListener {
            finish()
        }
        makeAppointmentBinding.recyclerDate.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        makeAppointmentBinding.recyclerDate.adapter = DateAdapter(dates){position->
            Log.d("@makeAppointment", "onCreate: ${dates[position].dayOfWeek}")
            daySelection = position
            makeAppointmentBinding.msg.visibility = View.GONE
            makeAppointmentBinding.slotView.visibility = View.VISIBLE

            val mTime = doctor.schedule[dates[position].dayOfWeek.lowercase()]?.get(0)?.hour.toString().uppercase()
            val aTime = doctor.schedule[dates[position].dayOfWeek.lowercase()]?.get(1)?.hour.toString().uppercase()
            val nTime = doctor.schedule[dates[position].dayOfWeek.lowercase()]?.get(2)?.hour.toString().uppercase()
            makeAppointmentBinding.mTime.text = "$mTime (${doctor.schedule[dates[position].dayOfWeek.lowercase()]?.get(0)?.duration} slot left)"
            makeAppointmentBinding.aTime.text = "$aTime (${doctor.schedule[dates[position].dayOfWeek.lowercase()]?.get(1)?.duration} slot left)"
            makeAppointmentBinding.nTime.text = "$nTime (${doctor.schedule[dates[position].dayOfWeek.lowercase()]?.get(2)?.duration} slot left)"
        }

        makeAppointmentBinding.morning.setOnClickListener {
            slotSelection = 0
            makeAppointmentBinding.morning.backgroundTintList = ContextCompat.getColorStateList(this@MakeAppointment,R.color.ui_blue)
            makeAppointmentBinding.mTime.setTextColor(ContextCompat.getColor(this@MakeAppointment,R.color.white))

            makeAppointmentBinding.aTime.setTextColor(ContextCompat.getColor(this@MakeAppointment,R.color.text_color))
            makeAppointmentBinding.nTime.setTextColor(ContextCompat.getColor(this@MakeAppointment,R.color.text_color))
            makeAppointmentBinding.afternoon.backgroundTintList = ContextCompat.getColorStateList(this@MakeAppointment,android.R.color.transparent)
            makeAppointmentBinding.night.backgroundTintList = ContextCompat.getColorStateList(this@MakeAppointment,android.R.color.transparent)
        }
        makeAppointmentBinding.afternoon.setOnClickListener {
            slotSelection = 1
            makeAppointmentBinding.afternoon.backgroundTintList = ContextCompat.getColorStateList(applicationContext,R.color.ui_blue)
            makeAppointmentBinding.aTime.setTextColor(ContextCompat.getColor(this@MakeAppointment,R.color.white))

            makeAppointmentBinding.mTime.setTextColor(ContextCompat.getColor(this@MakeAppointment,R.color.text_color))
            makeAppointmentBinding.nTime.setTextColor(ContextCompat.getColor(this@MakeAppointment,R.color.text_color))
            makeAppointmentBinding.morning.backgroundTintList = ContextCompat.getColorStateList(this@MakeAppointment,android.R.color.transparent)
            makeAppointmentBinding.night.backgroundTintList = ContextCompat.getColorStateList(this@MakeAppointment,android.R.color.transparent)
        }
        makeAppointmentBinding.night.setOnClickListener {
            slotSelection = 2
            makeAppointmentBinding.night.backgroundTintList = ContextCompat.getColorStateList(this@MakeAppointment,R.color.ui_blue)
            makeAppointmentBinding.nTime.setTextColor(ContextCompat.getColor(this@MakeAppointment,R.color.white))

            makeAppointmentBinding.aTime.setTextColor(ContextCompat.getColor(this@MakeAppointment,R.color.text_color))
            makeAppointmentBinding.mTime.setTextColor(ContextCompat.getColor(this@MakeAppointment,R.color.text_color))
            makeAppointmentBinding.morning.backgroundTintList = ContextCompat.getColorStateList(this@MakeAppointment,android.R.color.transparent)
            makeAppointmentBinding.afternoon.backgroundTintList = ContextCompat.getColorStateList(this@MakeAppointment,android.R.color.transparent)
        }

        makeAppointmentBinding.getAppointment.setOnClickListener {
            if(daySelection != -1 && slotSelection != -1 && paymentModeSelection != -1){
                if(paymentModeSelection == 0){
                    val wait = AlertDialog.Builder(this@MakeAppointment)
                    wait.setView(LayoutInflater.from(this).inflate(R.layout.appointment_loading,null))
                    wait.show()
                    val wait2 = wait.create()
                    Handler().postDelayed({
                        wait2.dismiss()
                        bookAppointment("not yet generated","offline","pending")
                    },4000)
                }else{
                    initializeRazorpay(doctor.fee)
                }

            }else{
                Toast.makeText(this@MakeAppointment,"Please select date and time",Toast.LENGTH_LONG).show()
            }
        }

        val adpeter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            paymentMode
        )

        // Set simple layout resource file for each item of spinner
        adpeter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        makeAppointmentBinding.snipper.adapter = adpeter
        makeAppointmentBinding.snipper.onItemSelectedListener = this

    }
    fun getNext30Days(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // You can change format
        val today = LocalDate.now()
        return (1 until 30).map { today.plusDays(it.toLong()).format(formatter) }
    }
    fun mapDatesToDayOfWeek(dateStrings: List<String>): List<DateInfo> {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

        return dateStrings.map { dateStr ->
            val date = LocalDate.parse(dateStr, inputFormatter)
            val formattedDate = date.format(outputFormatter)
            val dayOfWeek = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH)
            DateInfo(formattedDate, dayOfWeek)
        }
    }

    fun initializeRazorpay(fee:Int){
        val checkout = Checkout()
        checkout.setKeyID(rezorpay_api_key)

        try{
            val options = JSONObject()
            val retry = JSONObject()
            options.put("name","HEALTH SATHI")
            options.put("description", "Payment for Order booking your appointment")
            options.put("image","https://i.ibb.co/LdCrmPzz/app-logo.png")
            options.put("prefill.email", USER!!.uEmail)
            options.put("prefill.contact", USER!!.uPhone)
            options.put("theme.color", "#0165f7")
            options.put("currency", "INR")
            options.put("amount", "${fee*100}") // Amount in paise = fee*100
            retry.put("enable",true)
            retry.put("max_count",3)
            options.put("retry",retry)

            checkout.open(this@MakeAppointment,options)

        }catch (e:Exception){
            Log.d("@makeAppointment", "initializeRazorpay: "+e.message)
            Snackbar.make(makeAppointmentBinding.root,"Error in payment: "+e.message,Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        paymentModeSelection = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        paymentModeSelection = -1
    }



    private fun bookAppointment(paymentId: String?,paymentMode:String,paymentStatus:String) {

        val viewmodel by viewModels<DocViewModel>()
        val appointmentRequest = AppointmentRequestModel(
            uid = USER!!.uid,
            did = doctor.did,
            appointment_slot = doctor.schedule[dates[daySelection].dayOfWeek.lowercase()]?.get(slotSelection)?.time.toString(),
            appointment_date = formattedDate[daySelection],
            day = dates[daySelection].dayOfWeek.toString(),
            payment_id = paymentId!!,
            payment_mode = paymentMode,
            payment_status = paymentStatus,
            fee = doctor.fee
        )

        lifecycleScope.launch {
            val result = viewmodel.bookAppointment(appointmentRequest)
            if (result.isSuccessful){
               val msg = result.body()!!
                Log.d("@makeAppointment", "bookAppointment: ${msg.message}")
                startActivity(Intent(this@MakeAppointment,PaymentSuccess::class.java)
                    .putExtra("appointment",appointmentRequest)
                    .putExtra("doctor",doctor)
                )
                finish()
            }else{
                Snackbar.make(makeAppointmentBinding.root,"Error in payment",Snackbar.LENGTH_LONG).show()
            }

        }


    }
    override fun onPaymentSuccess(paymentId: String?) {
        bookAppointment(paymentId,"online","done")
        Snackbar.make(makeAppointmentBinding.root,"Payment Successfully",Snackbar.LENGTH_LONG).show()
    }

    override fun onPaymentError(code: Int, response: String?) {
        Snackbar.make(makeAppointmentBinding.root,"Payment failed",Snackbar.LENGTH_LONG).show()
    }
}

data class DateInfo(val date: String, val dayOfWeek: String)