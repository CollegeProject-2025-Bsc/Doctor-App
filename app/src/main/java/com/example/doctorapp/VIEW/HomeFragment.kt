package com.example.doctorapp.VIEW

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doctorapp.MODEL.BannerModel
import com.example.doctorapp.MODEL.DepartmentModel
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.adapter.BannerAdapter
import com.example.doctorapp.adapter.DepartmentAdapter
import com.example.doctorapp.adapter.DocViewCardAdapter
import com.example.doctorapp.databinding.FragmentHomeBinding


class HomeFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var homeFragmentBinding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        homeFragmentBinding = FragmentHomeBinding.inflate(layoutInflater)

        homeFragmentBinding.userName.text = "Hi, ${USER!!.uName}!"

        val department = arguments?.getSerializable("department") as List<DepartmentModel>?
        val banner = arguments?.getSerializable("banner") as List<BannerModel>?
        val popularDoctor = arguments?.getSerializable("popular") as List<DoctorModel>?

        Log.d("@data", department!![0].name)
        homeFragmentBinding.recyclerDep.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        homeFragmentBinding.recyclerDep.adapter = DepartmentAdapter(department = department!!,resources)
        homeFragmentBinding.viewPager.adapter = BannerAdapter(banner = banner!!)

        homeFragmentBinding.recyclerPopDoc.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL,false)
        homeFragmentBinding.recyclerPopDoc.adapter = DocViewCardAdapter(popularDoctor!!)
//        homeFragmentBinding.scrollView.viewTreeObserver.addOnScrollChangedListener {
//            val scrollY = homeFragmentBinding.scrollView.scrollY
//            val maxScroll = homeFragmentBinding.scrollView.getChildAt(0).measuredHeight - homeFragmentBinding.scrollView.measuredHeight
//
//            if (scrollY >= maxScroll) {
//                // Enable scrolling for RecyclerView
//                homeFragmentBinding.recyclerDep.isNestedScrollingEnabled = true
//            } else {
//                // Disable RecyclerView scrolling
//                homeFragmentBinding.recyclerDep.isNestedScrollingEnabled = false
//            }
//        }
        changeBannerPos(banner)

        homeFragmentBinding.searchBar.setOnClickListener {
            startActivity(Intent(context,SearchActivity::class.java).putExtra("department", ArrayList<DepartmentModel>(department!!)))
        }

        return homeFragmentBinding.root
    }

    private fun changeBannerPos(banner: List<BannerModel>) {
        Handler().postDelayed({
            val itemCount = banner!!.size
            val nextItem = (homeFragmentBinding.viewPager.currentItem + 1) % itemCount
            homeFragmentBinding.viewPager.setCurrentItem(nextItem, true)
            changeBannerPos(banner)
        },5000)
    }
}