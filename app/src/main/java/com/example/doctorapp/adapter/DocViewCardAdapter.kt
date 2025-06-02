package com.example.doctorapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.R
import com.example.doctorapp.VIEW.DoctorProfile

class DocViewCardAdapter(val doctor: List<DoctorModel>): RecyclerView.Adapter<DocViewCardAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
        lateinit var context: Context
        lateinit var docName: TextView
        lateinit var specialty: TextView
        lateinit var degrees: TextView
        lateinit var review: TextView
        lateinit var fee: TextView
        lateinit var docImg: ImageView
        lateinit var rating: RatingBar


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.doc_department_card, parent, false))
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        docName = holder.itemView.findViewById(R.id.doctorName)
        specialty = holder.itemView.findViewById(R.id.specialty)
        degrees = holder.itemView.findViewById(R.id.degrees)
        review = holder.itemView.findViewById(R.id.review)
        fee = holder.itemView.findViewById(R.id.rs)
        docImg = holder.itemView.findViewById(R.id.doctorImage)
        rating = holder.itemView.findViewById(R.id.rating)

        docName.text = doctor[position].dname
        specialty.text = doctor[position].specialization
        degrees.text = doctor[position].degree
        review.text = "(${doctor[position].rating} Reviews) "
        fee.text = "₹${doctor[position].fee}"
        rating.rating = doctor[position].rating.toFloat()

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context,DoctorProfile::class.java).putExtra("doctor",doctor[position]))
        }


        Glide.with(context).load(doctor[position].profile_pic).error(R.drawable.doctor_avatar).into(docImg)
    }

    override fun getItemCount(): Int = doctor.size


}