package com.example.doctorapp.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import com.example.doctorapp.R
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.doctorapp.MODEL.DepartmentModel
import com.example.doctorapp.VIEW.ViewDoctors

class DepartmentAdapter(
    val department: List<DepartmentModel>,
    val resources: Resources,
): RecyclerView.Adapter<DepartmentAdapter.DepViewHolder>() {


    inner class DepViewHolder(view:View): RecyclerView.ViewHolder(view)
        lateinit var depImage: ImageView
        lateinit var depName: TextView
        lateinit var depCard: LinearLayout
        lateinit var context: Context
        val colors = arrayOf(R.color.red,R.color.yellow,R.color.green,R.color.orange,R.color.pinkDeep)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DepViewHolder {
        context = parent.context
        return DepViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dep_view,parent,false))
    }

    override fun onBindViewHolder(holder: DepViewHolder, position: Int) {
        depImage = holder.itemView.findViewById(R.id.dep_icon)
        depName = holder.itemView.findViewById(R.id.dep_name)
        depCard = holder.itemView.findViewById(R.id.card)



        // set any default animation or custom animation (setSlideAnimation(ImageAnimationTypes.ZOOM_IN))

        val backgroundColor = colors[position % colors.size]
        //depCard.setBackgroundColor(resources.getColor(R.color.btn_blue)) //resources.getColor(backgroundColor)
        depImage.loadUrl(department[position].pic)
        depName.text = department[position].field

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ViewDoctors::class.java)
                .putExtra("dep_id",department[position].id)
                .putExtra("dep_name",department[position].name))
        }

    }
    override fun getItemCount() = department.size

    fun ImageView.loadUrl(url: String) {
        val imageLoader = ImageLoader.Builder(context)
            .components { add(SvgDecoder.Factory()) }
            .build()

        val request = ImageRequest.Builder(context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }
}