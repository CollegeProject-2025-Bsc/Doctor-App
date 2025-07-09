package com.example.doctorapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doctorapp.MODEL.AppointmentModel
import com.example.doctorapp.MODEL.ReviewModel
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class AppointmentAdapter(
    var appointment: List<AppointmentModel>,
    val lifecycleScope: LifecycleCoroutineScope,
    val viewmodel: DocViewModel,
    val clicked: (Int) -> Unit,): RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view)
        lateinit var pic:ImageView
        lateinit var docName:TextView
        lateinit var specialization:TextView
        lateinit var date:TextView
        lateinit var time:TextView
        lateinit var cancel:LinearLayout
        lateinit var review:LinearLayout
        lateinit var reschedule:LinearLayout
        lateinit var context: Context
        var index = 0

    override fun getItemViewType(position: Int): Int {
        return index
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context

        return if (viewType == 0){
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.appointment_card,parent,false))
        }else{
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.appointment_card_complete,parent,false))
        }
    }

    fun updateData(item: List<AppointmentModel>?, index: Int){
        appointment = item!!
        this.index = index
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        pic = holder.itemView.findViewById(R.id.pic)
        docName = holder.itemView.findViewById(R.id.docName)
        specialization = holder.itemView.findViewById(R.id.specialization)
        date = holder.itemView.findViewById(R.id.date)
        time = holder.itemView.findViewById(R.id.time)
        Glide.with(context)
            .load(appointment[position].pic)
            .error(R.drawable.doctor_avatar)
            .into(pic)

        docName.text = appointment[position].dname
        specialization.text = appointment[position].specialization
        date.text = "${formatDate(appointment[position].appointment_date)}"
        time.text = "${appointment[position].appointment_slot},${appointment[position].time}"



        if (index == 0){
            reschedule = holder.itemView.findViewById(R.id.reschedule)
            cancel = holder.itemView.findViewById(R.id.cancel)


            reschedule = holder.itemView.findViewById(R.id.reschedule)
            reschedule.setOnClickListener {
                val alert = MaterialAlertDialogBuilder(context).also {
                    it.setTitle("Change slot")
                    it.setMessage("If you want to change this slot please cancel this appointment then re-book a new appointment")
                }
                alert.show()
            }
            cancel.setOnClickListener {
                val alert = MaterialAlertDialogBuilder(context).also {
                    it.setTitle("Change slot")
                    it.setMessage("Do you want to cancel this slot")
                    it.setPositiveButton("yes"){
                            dialog,_ ->
                        clicked(position)
                        dialog.dismiss()
                    }
                }.create()
                alert.show()
            }
        }else{
            review = holder.itemView.findViewById(R.id.review)
            review.setOnClickListener {
                val dialog = BottomSheetDialog(context)
                val view = LayoutInflater.from(context).inflate(R.layout.review,null)
                dialog.setContentView(view)

                val reviewEditText = view.findViewById<EditText>(R.id.reviewEditText)
                val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
                val submitBtn = view.findViewById<Button>(R.id.submitReviewBtn)
                submitBtn.setOnClickListener {
                    val reviewText = reviewEditText.text.toString().trim()
                    val rating = ratingBar.rating

                    if (reviewText.isEmpty()) {
                        Snackbar.make(view,"Please write a review", Snackbar.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    if (rating == 0f) {
                        Snackbar.make(view,"Please give rating", Snackbar.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                    // 🚀 Submit to Firebase or local logic

                    val currentDate = LocalDate.now()
                    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy") // e.g., "08 July 2025"
                    val formattedDate = currentDate.format(formatter)

                    val review = ReviewModel(
                        did = appointment[position].doctor_id,
                        dep_id = appointment[position].dep_id,
                        uName = USER!!.uName,
                        review = reviewText,
                        rating = rating.toString(),
                        appointment_id = appointment[position].appointment_id,
                        date = formattedDate
                    )
                    lifecycleScope.launch {
                        viewmodel.addReview(review)

                    }
                    // Clear after submission
                    dialog.dismiss()
                    reviewEditText.text.clear()
                    ratingBar.rating = 0f
                }



                dialog.show()
            }
        }







    }

    override fun getItemCount(): Int = appointment.size

    fun formatDate(input: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("EEEE,MMM d", Locale.ENGLISH)

        val date = LocalDate.parse(input, inputFormatter)
        return date.format(outputFormatter)
    }
}