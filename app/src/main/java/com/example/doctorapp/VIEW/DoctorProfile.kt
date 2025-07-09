package com.example.doctorapp.VIEW

import android.content.Intent
import com.example.doctorapp.UTIL.Static.Companion.USER
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.R
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.adapter.ViewPagerAdapter
import com.example.doctorapp.databinding.ActivityDoctorViewAppointmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class DoctorProfile : AppCompatActivity() {
    lateinit var doctorProfileBinding: ActivityDoctorViewAppointmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewmodel = ViewModelProvider(this)[DocViewModel::class.java]
        doctorProfileBinding = ActivityDoctorViewAppointmentBinding.inflate(layoutInflater)
        setContentView(doctorProfileBinding.root)
        val doctor = intent.getSerializableExtra("doctor") as DoctorModel
        val dep_id = intent.getStringExtra("dep_id")
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

        doctorProfileBinding.viewPager.adapter = ViewPagerAdapter(this@DoctorProfile,tabTitle.size,doctor.about, dep_id = doctor.dep_id,did = doctor.did,doctor = doctor)

        doctorProfileBinding.experience.text = "${doctor.experience} Years"
        doctorProfileBinding.reviewsCount.text = doctor.rating.toString()

        doctorProfileBinding.location.setOnClickListener {
            val uri = Uri.parse("geo:0,0?q=${Uri.encode(doctor.caddress)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(mapIntent)
        }


        doctorProfileBinding.favAdd.setOnClickListener{
            lifecycleScope.launch {
                viewmodel.addFavDoc(USER!!.uid,doctor.did,doctor.specialization)
                Snackbar.make(doctorProfileBinding.root,"Doctor Added to favourite", Snackbar.LENGTH_LONG).show()
            }
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