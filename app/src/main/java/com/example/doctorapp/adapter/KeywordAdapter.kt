package com.example.doctorapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doctorapp.R

class KeywordAdapter(private val keywords: List<String>,val clicked:(String)->Unit) :
    RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder>() {

    inner class KeywordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val keywordText: TextView = itemView.findViewById(R.id.keywordText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_keyword, parent, false)
        return KeywordViewHolder(view)
    }

    override fun onBindViewHolder(holder: KeywordViewHolder, position: Int) {
        holder.keywordText.text = keywords[position]
        holder.itemView.setOnClickListener {
            Log.d("@search", "clicked")
            clicked(keywords[position])
        }
    }

    override fun getItemCount(): Int = keywords.size
}
