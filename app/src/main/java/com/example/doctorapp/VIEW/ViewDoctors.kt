package com.example.doctorapp.VIEW

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.ERROR
import com.example.doctorapp.UTIL.Static.Companion.HOMELOADING
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.adapter.DocViewCardAdapter
import com.example.doctorapp.databinding.ActivityViewDoctorsBinding
import kotlinx.coroutines.launch
import kotlin.getValue

class ViewDoctors : AppCompatActivity() {

    lateinit var viewDoctorsBinding: ActivityViewDoctorsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDoctorsBinding = ActivityViewDoctorsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(viewDoctorsBinding.root)
        val viewmodel by viewModels<DocViewModel>()

        val id = intent.getStringExtra("dep_id")

        viewDoctorsBinding.back.setOnClickListener{
            finish()
            overridePendingTransition(R.anim.act_zoom_in, R.anim.act_zoom_out)
        }
        val dep_name = intent.getStringExtra("dep_name").toString()
        viewDoctorsBinding.nDep.text = dep_name

        viewDoctorsBinding.ivLottie.setAnimationFromUrl(HOMELOADING)

        lifecycleScope.launch {
            val result = viewmodel.getDepartmentDoctor(id.toString())

            if (result.isSuccessful){
                viewDoctorsBinding.loading.visibility = View.GONE
                viewDoctorsBinding.mainScreen.visibility = View.VISIBLE
                viewDoctorsBinding.vRecycler.layoutManager = LinearLayoutManager(this@ViewDoctors)
                viewDoctorsBinding.vRecycler.adapter = DocViewCardAdapter(result.body()!!)
                Log.d("@viewDoctor", result.body().toString())
            }else{
                viewDoctorsBinding.ivLottie.setAnimationFromUrl(ERROR)
            }

        }


    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.act_zoom_in, R.anim.act_zoom_out)
    }
}