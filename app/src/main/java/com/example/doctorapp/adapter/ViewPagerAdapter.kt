package com.example.doctorapp.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.doctorapp.VIEW.AboutFragment
import com.example.doctorapp.VIEW.InfoFragment
import com.example.doctorapp.VIEW.ReviewsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, val size: Int, val about: String,): FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return if (position == 0){
            AboutFragment().also {
                it.arguments = Bundle().apply {
                    putString("about", about)
                }
            }
        }else if(position == 1){
            InfoFragment()
        }else{
            ReviewsFragment()
        }
    }

    override fun getItemCount(): Int = size


}
