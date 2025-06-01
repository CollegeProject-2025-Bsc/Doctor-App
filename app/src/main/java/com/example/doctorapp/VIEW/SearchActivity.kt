package com.example.doctorapp.VIEW

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doctorapp.MODEL.DepartmentModel
import com.example.doctorapp.MODEL.UserModel
import com.example.doctorapp.R
import com.example.doctorapp.adapter.DepartmentAdapter
import com.example.doctorapp.databinding.ActivitySearchBinding
import com.example.doctorapp.databinding.FragmentHomeBinding

class SearchActivity : AppCompatActivity() {
    lateinit var searchBinding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)
        val department = intent.getSerializableExtra("department") as? List<DepartmentModel>
        searchBinding.recyclerDep.layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL,false)
        searchBinding.recyclerDep.adapter = DepartmentAdapter(department = department!!,resources)

    }
}