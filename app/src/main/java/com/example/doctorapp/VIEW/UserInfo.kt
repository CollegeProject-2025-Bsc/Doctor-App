package com.example.doctorapp.VIEW

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.UTIL.mToast
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.databinding.ActivityUserInfoBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.getValue

class UserInfo : AppCompatActivity() {
    lateinit var firebaseUser: FirebaseUser
    private lateinit var userInfoBinding: ActivityUserInfoBinding

    var uName: String = ""
    var uProfilePic: String = ""
    lateinit var locationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest

    lateinit var pAlert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        userInfoBinding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(userInfoBinding.root)

        var uGender :String = ""
        var DOB :String = ""
        var uAddress: String = ""
        var uEmail: String = ""
        var uPhone: String = ""
        var uLat: Float = 0.0f
        var uLong: Float = 0.0f
        var isUserPhoneVerified = false
        var isDetailsFilled = false
        val viewModel by viewModels<DocViewModel>()




        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object :LocationCallback(){
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                for (location in result.locations){
                    uLat = location.latitude.toFloat()
                    uLong = location.longitude.toFloat()
                    val geoCoder = Geocoder(this@UserInfo, Locale.getDefault())
                    val address = geoCoder.getFromLocation(location.latitude,location.longitude,1)

                    if(address != null && address.isNotEmpty()){
                        val temp = address?.get(0)
                        uAddress = temp!!.getAddressLine(0)
                        userInfoBinding.uAddress.setText(uAddress)
                    }else{
                        Snackbar.make(userInfoBinding.main,"Unable to fetch location",Snackbar.LENGTH_SHORT).show()
                    }


                    pAlert.dismiss()
                }
                locationClient.removeLocationUpdates(this)
            }
        }
        pAlert = AlertDialog.Builder(this).create()
        pAlert.setCancelable(false)
        pAlert.setView(LayoutInflater.from(this).inflate(R.layout.progress_alert, null))
        
        uPhone = USER!!.uPhone
        uEmail = USER!!.uEmail
        uName = USER!!.uName
        uProfilePic = USER!!.uProfilePic

        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        try {
            if (uName.isNotBlank()){
                userInfoBinding.uName.setText(uName)
            }

            userInfoBinding.mail.text = uEmail
            userInfoBinding.phone.text = uPhone
            userInfoBinding.uPhone.setText(uPhone)
            setImage(uProfilePic,userInfoBinding.profilePic)

            userInfoBinding.submit.setOnClickListener {
                if (uName.isNotEmpty() && uGender.isNotEmpty() && DOB.isNotEmpty() && uAddress.isNotEmpty() && uEmail.isNotEmpty() && uPhone.isNotEmpty()){
                    updateUser(userInfoBinding.uName.text.toString(),uGender,DOB,uAddress,uLat,uLong,uEmail,uPhone,firebaseUser,viewModel)
                }else{
                    Snackbar.make(userInfoBinding.main,"Please fill all the fields",Snackbar.LENGTH_SHORT).show()
                }
            }


            userInfoBinding.uAge.setOnClickListener {
                val datePicker = DatePickerDialog(this, {
                        _, selectedYear, selectedMonth, selectedDay ->
                    DOB = "$selectedDay/${selectedMonth+1}/$selectedYear"
                    userInfoBinding.uAge.setText(DOB)
                    Log.d("@DOB", DOB)
                },year,month,day)
                datePicker.setTitle("Select your Date of Birth")
                datePicker.show()
            }
            userInfoBinding.uGender.setOnClickListener {
                val menu = PopupMenu(this@UserInfo,it )
                menu.menu.add("Male") // menus items
                menu.menu.add("Female") // menus items
                menu.menu.add("Don't want to share")
                menu.setOnMenuItemClickListener {
                    uGender = it.title.toString()
                    userInfoBinding.uGender.setText(it.title)
                    true
                }
                menu.show()
            }

            userInfoBinding.picBtn.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
            }
            userInfoBinding.fetch.setOnClickListener {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if (checkGPS()){
                        fetchLocation()
                    }else{
                        buildAlertMessageNoGps()
                    }

                }else{
                    val alert = MaterialAlertDialogBuilder(this@UserInfo)
                    alert.setTitle("NEED USER PERMISSION")
                    alert.setMessage("We need location permission to fetch your location")
                    alert.setPositiveButton ("ALLOW"){dialog,_ ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100)
                    }
                    alert.setNegativeButton("DENY"){dialog,_ ->
                        dialog.dismiss()
                    }
                    alert.show()

                }
            }

        }catch (e:Exception){
            Snackbar.make(this,userInfoBinding.main,e.message.toString(),Snackbar.LENGTH_SHORT).show()
        }



    }

    private fun setImage(picLink: String, container: ImageView) {
        Glide.with(this@UserInfo)
            .load(picLink)
            .error(R.drawable.male_avatar)
            .into(container)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Log.d("@data", "here")
            userInfoBinding.profilePic.setImageURI(data!!.data)
            uProfilePic = data.data.toString()
        }
    }
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchLocation() {
        pAlert.show()
        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (checkGPS()){
                fetchLocation()
            }else{
                buildAlertMessageNoGps()
            }

        }else{
            val alert = AlertDialog.Builder(this@UserInfo)
            alert.setMessage("Location permission is required for fetching your location")
            alert.setPositiveButton("Allow Permission") {
                dialog,_ ->
                openAppSettings(this)
            }
            alert.setNegativeButton("Cancel") {
                dialog,_ ->
                dialog.dismiss()
            }
            alert.show()
        }
    }
    private fun buildAlertMessageNoGps() {
        val alert = MaterialAlertDialogBuilder(this)
        alert.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") {dialog, id ->
                dialog.dismiss()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") {dialog, id ->
                dialog.dismiss()
            }
        alert.show()
    }
    private fun updateUser(
        uName: String,
        uGender: String,
        DOB: String,
        uAddress: String,
        lat: Float,
        long: Float,
        uEmail: String,
        uPhone: String,
        user: FirebaseUser,
        viewModel: DocViewModel
    ) {
        lifecycleScope.launch {
            USER!!.uName = uName
            USER!!.uGender = uGender
            USER!!.DOB = DOB
            USER!!.uAddress = uAddress
            USER!!.uLat = lat
            USER!!.uLong = long
            USER!!.uEmail = uEmail
            USER!!.uPhone = uPhone
            USER!!.uProfilePic = uProfilePic
            USER!!.isDetailsFilled = true
            val result = viewModel.updateUser(user = USER!!)
            if (result.isSuccessful){
                startActivity(Intent(this@UserInfo,Home::class.java))
            }else{
                Snackbar.make(userInfoBinding.main,result.message(),Snackbar.LENGTH_SHORT).show()
            }

        }
    }
}


fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}


private fun UserInfo.checkGPS(): Boolean {
    val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}


