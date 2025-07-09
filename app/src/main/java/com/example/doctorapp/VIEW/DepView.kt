package com.example.doctorapp.VIEW

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.doctorapp.MODEL.DepartmentModel
import com.example.doctorapp.R
import com.example.doctorapp.adapter.DepartmentAdapter
import com.example.doctorapp.databinding.ActivityDepViewBinding

class DepView : AppCompatActivity() {
    lateinit var depViewBinding: ActivityDepViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        depViewBinding = ActivityDepViewBinding.inflate(layoutInflater)
        setContentView(depViewBinding.root)

        val department = intent.getSerializableExtra("department") as List<DepartmentModel>

        depViewBinding.depR.layoutManager = GridLayoutManager(this,4)
        depViewBinding.depR.adapter = DepartmentAdapter(department ,resources)

        depViewBinding.back.setOnClickListener {
            finish()
        }

    }
}