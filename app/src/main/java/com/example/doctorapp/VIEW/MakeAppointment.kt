package com.example.doctorapp.VIEW

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.R
import com.example.doctorapp.adapter.DateAdapter
import com.example.doctorapp.databinding.ActivityMakeAppointmentBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MakeAppointment : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var makeAppointmentBinding: ActivityMakeAppointmentBinding

    var slotSelection: Int = -1
    var daySelection = -1
    var paymentModeSelection = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        makeAppointmentBinding = ActivityMakeAppointmentBinding.inflate(layoutInflater)
        setContentView(makeAppointmentBinding.root)
        val formattedDate = getNext30Days()
        val dates = mapDatesToDayOfWeek(formattedDate)
        val doctor = intent.getSerializableExtra("doctor") as DoctorModel

        val paymentMode = listOf(
            "Pay at the time of appointment",
            "Pay via RazorPay",
        )
        val payment = listOf("offline","online")

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
                Log.d("@makeAppointment", "${payment[paymentModeSelection]} ${dates[daySelection].dayOfWeek} ${formattedDate[daySelection]} ${doctor.schedule[dates[daySelection].dayOfWeek.lowercase()]?.get(slotSelection)?.hour.toString()}")
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        paymentModeSelection = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        paymentModeSelection = 0
    }
}

data class DateInfo(val date: String, val dayOfWeek: String)