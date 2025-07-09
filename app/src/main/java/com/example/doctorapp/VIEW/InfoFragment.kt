package com.example.doctorapp.VIEW

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.R
import com.example.doctorapp.databinding.FragmentInfoBinding


class InfoFragment : Fragment() {

lateinit var infoFragmentBinding: FragmentInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        infoFragmentBinding = FragmentInfoBinding.inflate(layoutInflater)

        val doctor = arguments?.getSerializable("doctor") as DoctorModel

        infoFragmentBinding.address.text = doctor.caddress
        infoFragmentBinding.email.text = doctor.cemail
        infoFragmentBinding.contact.text = doctor.cphone


        return infoFragmentBinding.root
    }

}