package com.example.doctorapp.VIEW

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.doctorapp.MODEL.BannerModel
import com.example.doctorapp.MODEL.DepartmentModel
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.ERROR
import com.example.doctorapp.UTIL.Static.Companion.HOMELOADING
import com.example.doctorapp.UTIL.Static.Companion.LOSTCONN
import com.example.doctorapp.UTIL.Static.Companion.TAB_ANI_DURATION
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.http2.Http2Reader

class Home : AppCompatActivity() {
    private lateinit var homeBinding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        val viewmodel by viewModels<DocViewModel>()
        var tabIndex = 0
        var department: List<DepartmentModel>? = emptyList<DepartmentModel>()
        var popularDoctor: List<DoctorModel>? = emptyList<DoctorModel>()
        var banners: List<BannerModel>? = emptyList<BannerModel>()




        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.setSystemUiVisibility(uiOptions)
        swapFrame(LoadingError(HOMELOADING))
        Handler().postDelayed({
            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val departmentResult = viewmodel.getCategory()
                        val popularDoctorResult = viewmodel.getPopularDoctors()
                        val bannerResult = viewmodel.getBanner()
                        if (departmentResult.isSuccessful && popularDoctorResult.isSuccessful && bannerResult.isSuccessful){
                            department = departmentResult.body()
                            popularDoctor = popularDoctorResult.body()
                            banners = bannerResult.body()
                            Log.d("@pop", "onCreate: $popularDoctor")
                            Log.d("@pop", "onCreate: ${popularDoctorResult.body()}")
                            swapFrame(HomeFragment(department,popularDoctor,banners))
                        }else{
                            swapFrame(LoadingError(LOSTCONN))
                            Snackbar.make(homeBinding.root,departmentResult.message(), Snackbar.LENGTH_LONG).show()
                            Log.d("@HOME", "onCreate: ${departmentResult.message()}")
                        }
                    }
                }catch (e:Exception){
                    swapFrame(LoadingError(ERROR))
                    Snackbar.make(homeBinding.root,e.message.toString(), Snackbar.LENGTH_LONG).show()
                    Log.d("@HOME", "onCreate: ${e.message}")
                }
            }
        },2000)


        //setting home tab

        homeBinding.homeIcon.setColorFilter(ContextCompat.getColor(this, R.color.ui_blue), android.graphics.PorterDuff.Mode.SRC_IN)
        homeBinding.homeText.visibility = View.VISIBLE





        //bottom navigation

        homeBinding.home.setOnClickListener {
            if (tabIndex != 0){
                swapFrame(HomeFragment(department,popularDoctor,banners))
                tabIndex = 0
            }
            val transition = ScaleAnimation(0f,1f,0f,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
            transition.apply {
                duration = TAB_ANI_DURATION
                fillAfter = true
            }
            homeBinding.home.startAnimation(transition)
            homeBinding.homeIcon.setColorFilter(ContextCompat.getColor(this, R.color.ui_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.homeText.visibility = View.VISIBLE




            homeBinding.favIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.favText.visibility = View.GONE

            homeBinding.appointmentIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.appointmentText.visibility = View.GONE

            homeBinding.profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.profileText.visibility = View.GONE

        }
        homeBinding.fav.setOnClickListener {
            if (tabIndex != 1){
                swapFrame(FavDocFragment())
                tabIndex = 1
            }
            val transition = ScaleAnimation(0f,1f,0f,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
            transition.apply {
                duration = TAB_ANI_DURATION
                fillAfter = true
            }
            homeBinding.fav.startAnimation(transition)
            homeBinding.favIcon.setColorFilter(ContextCompat.getColor(this, R.color.ui_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.favText.visibility = View.VISIBLE




            homeBinding.homeIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.homeText.visibility = View.GONE

            homeBinding.appointmentIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.appointmentText.visibility = View.GONE

            homeBinding.profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.profileText.visibility = View.GONE


        }
        homeBinding.appoinment.setOnClickListener {
            if (tabIndex != 2){
                swapFrame(AppointmentFragment())
                tabIndex = 2
            }
            val transition = ScaleAnimation(0f,1f,0f,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
            transition.apply {
                duration = TAB_ANI_DURATION
                fillAfter = true
            }
            homeBinding.appoinment.startAnimation(transition)
            homeBinding.appointmentIcon.setColorFilter(ContextCompat.getColor(this, R.color.ui_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.appointmentText.visibility = View.VISIBLE




            homeBinding.homeIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.homeText.visibility = View.GONE

            homeBinding.favIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.favText.visibility = View.GONE

            homeBinding.profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.profileText.visibility = View.GONE
        }
        homeBinding.profile.setOnClickListener {
            startActivity(Intent(this@Home,UserProfile::class.java))
            overridePendingTransition(R.anim.act_zoom_out,R.anim.act_zoom_in)
            tabIndex = 0
            if(department!!.isNotEmpty() && popularDoctor!!.isNotEmpty()){
                swapFrame(HomeFragment(department,popularDoctor,banners))
            }

            val transition = ScaleAnimation(0f,1f,0f,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
            transition.apply {
                duration = TAB_ANI_DURATION
                fillAfter = true
            }
            homeBinding.home.startAnimation(transition)
            homeBinding.homeIcon.setColorFilter(ContextCompat.getColor(this, R.color.ui_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.homeText.visibility = View.VISIBLE




            homeBinding.favIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.favText.visibility = View.GONE

            homeBinding.appointmentIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_color), android.graphics.PorterDuff.Mode.SRC_IN)
            homeBinding.appointmentText.visibility = View.GONE
        }

    }
    fun swapFrame(frame: androidx.fragment.app.Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame,frame)
        fragmentTransaction.commit()
    }
}