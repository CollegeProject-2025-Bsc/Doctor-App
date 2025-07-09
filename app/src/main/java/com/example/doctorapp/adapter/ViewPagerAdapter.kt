package com.example.doctorapp.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.VIEW.AboutFragment
import com.example.doctorapp.VIEW.InfoFragment
import com.example.doctorapp.VIEW.ReviewsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, val size: Int, val about: String,val dep_id:String, val did:String, val doctor: DoctorModel): FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return if (position == 0){
            AboutFragment().also {
                it.arguments = Bundle().apply {
                    putString("about", about)
                }
            }
        }else if(position == 1){
            InfoFragment().also {
                it.arguments = Bundle().apply{
                    putSerializable("doctor",doctor)
                }
            }
        }else{
            ReviewsFragment().also {
                it.arguments = Bundle().apply {
                    putString("dep_id",dep_id)
                    putString("did",did)
                }
            }
        }
    }

    override fun getItemCount(): Int = size


}
