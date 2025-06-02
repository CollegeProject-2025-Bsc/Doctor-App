package com.example.doctorapp.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.doctorapp.R
import com.example.doctorapp.VIEW.DateInfo

class DateAdapter(
    val date:List<DateInfo>,
    val onClick:(Int)-> Unit
):RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    var pos = -1

    inner class DateViewHolder(val itemView: View):RecyclerView.ViewHolder(itemView){
        lateinit var  dateView :TextView
        lateinit var  dayView :TextView
        lateinit var  container : LinearLayout
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DateViewHolder {
        return DateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.date_view,parent,false)
        )
    }

    override fun onBindViewHolder(holder: DateAdapter.DateViewHolder, position: Int) {
        holder.dateView = holder.itemView.findViewById(R.id.date)
        holder.dayView = holder.itemView.findViewById(R.id.day)
        holder.dayView = holder.itemView.findViewById(R.id.day)
        holder.container = holder.itemView.findViewById(R.id.container)

        holder.dateView.text = date[position].date.substring(0,2)
        holder.dayView.text = date[position].dayOfWeek.substring(0,3)
        holder.itemView.setOnClickListener {
            pos = position
            notifyDataSetChanged()
            Log.d("@dataAdapter", "onBindViewHolder: clicked $position")
            onClick(position)
        }

        if (position == pos){
            holder.container.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context,R.color.ui_blue)
            holder.dateView.setTextColor(Color.WHITE)
            holder.dayView.setTextColor(Color.WHITE)
        }else{
            ViewCompat.setBackgroundTintList(holder.container, ColorStateList.valueOf(android.R.color.transparent))
            holder.dateView.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.text_color))
            holder.dayView.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.ui_gray))
        }


    }

    override fun getItemCount(): Int = date.size
}