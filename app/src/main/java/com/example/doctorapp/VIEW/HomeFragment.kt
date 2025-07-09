package com.example.doctorapp.VIEW

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.doctorapp.MODEL.BannerModel
import com.example.doctorapp.MODEL.DepartmentModel
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.adapter.BannerAdapter
import com.example.doctorapp.adapter.DepartmentAdapter
import com.example.doctorapp.adapter.DocViewCardAdapter
import com.example.doctorapp.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File


class HomeFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var homeFragmentBinding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeFragmentBinding = FragmentHomeBinding.inflate(layoutInflater)
        homeFragmentBinding.userName.text = "Hi, ${USER!!.uName} !"

//        try{
//            if (USER!!.uProfilePic.isNotEmpty()){
//                Glide.with(requireContext())
//                    .load(USER!!.uProfilePic)
//                    .centerCrop()
//                    .into(homeFragmentBinding.uPic)
//                Log.d("@imgUri", "onCreateView: ${USER!!.uProfilePic}")
//            }else{
//                Snackbar.make(homeFragmentBinding.root,"Image uri null", Snackbar.LENGTH_LONG).show()
//            }
//
//        }catch (e: Exception){
//            Snackbar.make(homeFragmentBinding.root,e.message.toString(), Snackbar.LENGTH_LONG).show()
//        }
        try{
            val path = USER!!.uProfilePic
            val file = File(path)

            if (file.exists()) {
                Glide.with(requireContext())
                    .load(file)
                    .centerCrop()
                    .error(R.drawable.male_avatar)
                    .into(homeFragmentBinding.uPic)
            } else {
                Log.e("ImageLoad", "File not found: $path")
            }

        }catch(e: Exception){
            Snackbar.make(homeFragmentBinding.root,"Error", Snackbar.LENGTH_LONG).show()
        }
        val department = arguments?.getSerializable("department") as List<DepartmentModel>?
        val banner = arguments?.getSerializable("banner") as List<BannerModel>?
        val popularDoctor = arguments?.getSerializable("popular") as List<DoctorModel>?

        Log.d("@data", department!![0].name)
        homeFragmentBinding.recyclerDep.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        homeFragmentBinding.recyclerDep.adapter = DepartmentAdapter(department = department!!,resources)
        homeFragmentBinding.viewPager.adapter = BannerAdapter(banner = banner!!)

        homeFragmentBinding.recyclerPopDoc.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL,false)
        homeFragmentBinding.recyclerPopDoc.adapter = DocViewCardAdapter(popularDoctor!!){

        }
        changeBannerPos(banner)

        homeFragmentBinding.searchBar.setOnClickListener {
            startActivity(Intent(context,SearchActivity::class.java).putExtra("department", ArrayList<DepartmentModel>(department!!)))
        }

        homeFragmentBinding.dSeeAll.setOnClickListener {
            startActivity(Intent(requireContext(), DepView::class.java).putExtra("department", department!! as ArrayList<DepartmentModel>))
        }

        return homeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = view.findViewById<ImageView>(R.id.uPic)

        if (!USER!!.uProfilePic.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(USER!!.uProfilePic)

                try {
                    if (!USER!!.uProfilePic.isNullOrEmpty()) {
                        val file = File(USER!!.uProfilePic)
                        if (file.exists()) {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            if (bitmap!=null){
                                Glide.with(requireContext())
                                    .load(bitmap)
                                    .centerCrop()
                                    .error(R.drawable.male_avatar)
                                    .into(imageView)
                            }

                        }
                    }else {
                        Snackbar.make(view, "Failed to open image stream", Snackbar.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Snackbar.make(view, "Glide error: ${e.message}", Snackbar.LENGTH_LONG).show()
                }


            } catch (e: Exception) {
                Snackbar.make(view, "Glide error: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        } else {
            Snackbar.make(view, "Profile picture URI is empty", Snackbar.LENGTH_SHORT).show()
        }
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