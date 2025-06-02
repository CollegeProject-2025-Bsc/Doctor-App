package com.example.doctorapp.VIEW

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.R
import com.example.doctorapp.adapter.ViewPagerAdapter
import com.example.doctorapp.databinding.ActivityDoctorViewAppointmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class DoctorProfile : AppCompatActivity() {
    lateinit var doctorProfileBinding: ActivityDoctorViewAppointmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        doctorProfileBinding = ActivityDoctorViewAppointmentBinding.inflate(layoutInflater)
        setContentView(doctorProfileBinding.root)
        val doctor = intent.getSerializableExtra("doctor") as DoctorModel
        Log.d("@doctorProfile", "onCreate: $doctor")

        doctorProfileBinding.back.setOnClickListener {
            finish()
        }

        doctorProfileBinding.fee.text = "₹${doctor.fee}"
        doctorProfileBinding.dname.text = doctor.dname
        doctorProfileBinding.degree.text = doctor.degree
        doctorProfileBinding.department.text = doctor.specialization
        doctorProfileBinding.fee.text = "₹${doctor.fee}"


        val tabTitle = listOf("About","Info","Reviews")

        doctorProfileBinding.viewPager.adapter = ViewPagerAdapter(this@DoctorProfile,tabTitle.size,doctor.about)

        doctorProfileBinding.experience.text = "${doctor.experience} Years"
        doctorProfileBinding.reviewsCount.text = doctor.rating.toString()

        doctorProfileBinding.location.setOnClickListener {
            val uri = Uri.parse("geo:0,0?q=${Uri.encode(doctor.caddress)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(mapIntent)
        }

        doctorProfileBinding.getAppointment.setOnClickListener {
            startActivity(Intent(this,MakeAppointment::class.java).putExtra("doctor",doctor))
        }

        TabLayoutMediator(doctorProfileBinding.tabLayout, doctorProfileBinding.viewPager){tab, position ->
                tab.text = tabTitle[position]
        }.attach()

        Glide.with(this).load(doctor.profile_pic).error(R.drawable.doctor_avatar).into(doctorProfileBinding.docImg)

    }
}