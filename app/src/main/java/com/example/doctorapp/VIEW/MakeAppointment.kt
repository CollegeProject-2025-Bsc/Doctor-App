package com.example.doctorapp.VIEW

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doctorapp.adapter.DateAdapter
import com.example.doctorapp.databinding.ActivityMakeAppointmentBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MakeAppointment : AppCompatActivity() {
    lateinit var makeAppointmentBinding: ActivityMakeAppointmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        makeAppointmentBinding = ActivityMakeAppointmentBinding.inflate(layoutInflater)
        setContentView(makeAppointmentBinding.root)
        val dates = mapDatesToDayOfWeek(getNext30Days())

        makeAppointmentBinding.back.setOnClickListener {
            finish()
        }
        makeAppointmentBinding.recyclerDate.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        makeAppointmentBinding.recyclerDate.adapter = DateAdapter(dates)


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
}

data class DateInfo(val date: String, val dayOfWeek: String)