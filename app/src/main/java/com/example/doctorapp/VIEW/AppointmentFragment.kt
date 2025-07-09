package com.example.doctorapp.VIEW

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doctorapp.MODEL.AppointmentModel
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.ERROR
import com.example.doctorapp.UTIL.Static.Companion.LOADING
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.adapter.AppointmentAdapter
import com.example.doctorapp.databinding.FragmentAppointmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.ArrayList


class AppointmentFragment : Fragment() {

    lateinit var appointmentFragmentBinding: FragmentAppointmentBinding
    lateinit var adapter : AppointmentAdapter

    val UPCOMING = 0
    val COMPLETE = 1
    var selectedTab = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        appointmentFragmentBinding = FragmentAppointmentBinding.inflate(layoutInflater)

        var appointmentUpcoming: ArrayList<AppointmentModel>? = arrayListOf()
        var appointmentDone: List<AppointmentModel>? = emptyList<AppointmentModel>()
        var appointment: List<AppointmentModel>? = emptyList<AppointmentModel>()

        val viewModel = arguments?.getSerializable("viewmodel") as DocViewModel

        appointmentFragmentBinding.ivLottie.setAnimationFromUrl(LOADING)

        lifecycleScope.launch {
            if(isAdded){
                val appointmentResult = viewModel.getAppointmentFromUser(USER!!.uid)
                if (appointmentResult.isSuccessful){
                    appointment = appointmentResult.body()!!.sortedBy {
                        LocalDate.parse(it.appointment_date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }
                    val (appointmentD, appointmentU) = splitAppointments(appointment,viewModel)
                    appointmentUpcoming = appointmentU as ArrayList<AppointmentModel>?
                    appointmentDone = appointmentD
                    appointmentFragmentBinding.load.visibility = View.GONE
                    appointmentFragmentBinding.appoint.visibility = View.VISIBLE
                    appointmentFragmentBinding.appointmentRecycler.layoutManager = LinearLayoutManager(requireContext())
                    adapter = AppointmentAdapter(appointmentUpcoming!!,lifecycleScope,viewModel){
                        lifecycleScope.launch {
                            Log.d("@delAppo", "$it")
                            viewModel.deleteAppointment(appointmentUpcoming!![it].appointment_id,USER!!.uEmail)
                            appointmentUpcoming!!.removeAt(it)
                            adapter.updateData(appointmentUpcoming,UPCOMING)
                            Snackbar.make(appointmentFragmentBinding.root,"Your appointment successfully cancelled", Snackbar.LENGTH_LONG).show()
                        }
                    }
                    appointmentFragmentBinding.appointmentRecycler.adapter = adapter
                }
            }else{
                appointmentFragmentBinding.ivLottie.setAnimationFromUrl(ERROR)
            }
        }
        appointmentFragmentBinding.complete.setOnClickListener {
            selectedTab = 1
            appointmentFragmentBinding.complete.getBackground().setTint(ContextCompat.getColor(requireContext(),R.color.ui_blue))
            appointmentFragmentBinding.upcoming.getBackground().setTint(ContextCompat.getColor(requireContext(),android.R.color.transparent))

            appointmentFragmentBinding.ct.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            appointmentFragmentBinding.ut.setTextColor(ContextCompat.getColor(requireContext(),R.color.ui_gray))
            adapter.updateData(appointmentDone, COMPLETE)
        }
        appointmentFragmentBinding.upcoming.setOnClickListener {
            selectedTab = 0
            appointmentFragmentBinding.upcoming.getBackground().setTint(ContextCompat.getColor(requireContext(),R.color.ui_blue))
            appointmentFragmentBinding.complete.getBackground().setTint(ContextCompat.getColor(requireContext(),android.R.color.transparent))

            appointmentFragmentBinding.ut.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            appointmentFragmentBinding.ct.setTextColor(ContextCompat.getColor(requireContext(),R.color.ui_gray))
            adapter.updateData(appointmentUpcoming, UPCOMING)
        }
        return appointmentFragmentBinding.root
    }

    fun splitAppointments(appointments: List<AppointmentModel>?, viewModel: DocViewModel, ): Pair<List<AppointmentModel>, List<AppointmentModel>> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()

        val twoDaysAgo = today.minusDays(2) // 2025-07-07
        val oneDayAgo = today.minusDays(1)  // 2025-07-08

        val completed = mutableListOf<AppointmentModel>()
        val upcoming = mutableListOf<AppointmentModel>()

        if (appointments != null) {
            for (appointment in appointments) {
                val appointmentDate = LocalDate.parse(appointment.appointment_date, formatter)
                when {
                    appointmentDate >= today -> upcoming.add(appointment) // upcoming: today or future
                    appointmentDate in twoDaysAgo..oneDayAgo -> completed.add(appointment) // only 2 days old
                    else -> {
                        lifecycleScope.launch {
                            viewModel.deleteAppointment(appointment.appointment_id,USER!!.uEmail)
                        }
                    } // drop anything older than 2 days ago
                }
            }
        }

        return Pair(completed, upcoming)
    }

}