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
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.chaos.view.PinView
import com.example.doctorapp.MODEL.PhoneModel
import com.example.doctorapp.R
import com.example.doctorapp.UTIL.Static.Companion.LOADING
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.UTIL.mToast
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.databinding.ActivityUserProfile2Binding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.collections.isNotEmpty
import kotlin.getValue
import kotlin.math.log

class UserProfileEdit : AppCompatActivity() {

    lateinit var userProfileEditBinding: ActivityUserProfile2Binding
    lateinit var locationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest

    lateinit var pAlert: AlertDialog
    var uProfilePic = ""
    val viewmodel by viewModels<DocViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        userProfileEditBinding = ActivityUserProfile2Binding.inflate(layoutInflater)


        setContentView(userProfileEditBinding.root)




        var uLat: Float = 0.0f
        var uLong: Float = 0.0f
        val uPhone = USER!!.uPhone
        val uEmail = USER!!.uEmail
        val uName = USER!!.uName
        uProfilePic = USER!!.uProfilePic
        var uAddress = USER!!.uAddress

        var DOB = USER!!.DOB
        var gender = USER!!.uGender
        userProfileEditBinding.ccp.registerCarrierNumberEditText(userProfileEditBinding.number)
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
                    val geoCoder = Geocoder(this@UserProfileEdit, Locale.getDefault())
                    val address = geoCoder.getFromLocation(location.latitude,location.longitude,1)

                    if(address != null && address.isNotEmpty()){
                        val temp = address?.get(0)
                        uAddress = temp!!.getAddressLine(0)
                        userProfileEditBinding.uAddress.setText(uAddress)
                    }else{
                        Snackbar.make(userProfileEditBinding.main,"Unable to fetch location",Snackbar.LENGTH_SHORT).show()
                    }


                    pAlert.dismiss()
                }
                locationClient.removeLocationUpdates(this)
            }
        }
        pAlert = AlertDialog.Builder(this).create()
        pAlert.setCancelable(false)
        pAlert.setView(LayoutInflater.from(this).inflate(R.layout.progress_alert, null))



        val path = USER!!.uProfilePic
        val file = File(path)

        if (file.exists()) {
            Glide.with(this)
                .load(file)
                .centerCrop()
                .error(R.drawable.male_avatar)
                .into(userProfileEditBinding.profilePic)
        } else {
            Log.e("ImageLoad", "File not found: $path")
        }


        userProfileEditBinding.uName.setText(uName)
        userProfileEditBinding.mail.setText(uEmail)
        userProfileEditBinding.phone.setText(uPhone)
        userProfileEditBinding.uAddress.setText(uAddress)
        userProfileEditBinding.number.setText(uPhone.replaceFirst("+91", ""))

        userProfileEditBinding.uAge.setText(DOB)
        userProfileEditBinding.uGender.setText(gender)

        userProfileEditBinding.cancel.setOnClickListener {
            finish()
        }

        userProfileEditBinding.update.setOnClickListener {

            val alert = MaterialAlertDialogBuilder(this)

            val view = LayoutInflater.from(this).inflate(R.layout.fragment_loading_error,null)
            val ivTv = view.findViewById<LottieAnimationView>(R.id.ivLottie)

            ivTv.setAnimationFromUrl(LOADING)

            alert.setView(view)
            alert.setCancelable(false)
            alert.show()

            if (userProfileEditBinding.ccp.isValidFullNumber) {
                if(uPhone != userProfileEditBinding.ccp.fullNumberWithPlus){
                    lifecycleScope.launch {
                        val resultOtp = viewmodel.getOtp(PhoneModel(userProfileEditBinding.ccp.fullNumberWithPlus))
                        val otp = resultOtp.body()!!.otp

                        val alert = MaterialAlertDialogBuilder(this@UserProfileEdit)
                        val view = LayoutInflater.from(this@UserProfileEdit).inflate(R.layout.otp_view,null)
                        alert.setView(view)
                        val alertC = alert.create()
                        val otpSubmit = view.findViewById<Button>(R.id.optSubmit)
                        if (resultOtp.isSuccessful){
                            alertC.show()
                            otpSubmit.setOnClickListener {
                                if(otp == view.findViewById<PinView>(R.id.pinview).text.toString()){
                                    view.findViewById<PinView>(R.id.pinview).setLineColor(
                                        ResourcesCompat.getColor(getResources(), R.color.ui_green, getTheme()))
                                    Handler().postDelayed({
                                        alertC.dismiss()
                                        updateUser(
                                            USER!!.uid,
                                            userProfileEditBinding.uName.text.toString(),
                                            userProfileEditBinding.uGender.text.toString(),
                                            userProfileEditBinding.uAge.text.toString(),
                                            userProfileEditBinding.uAddress.text.toString(),
                                            uLat,
                                            uLong,
                                            USER!!.uEmail,
                                            userProfileEditBinding.ccp.fullNumberWithPlus,
                                            uProfilePic,
                                            true,
                                            true
                                            )
                                    },1000)
                                }else{
                                    alertC.dismiss()
                                    Snackbar.make(userProfileEditBinding.root,"Mismatched otp",
                                        Snackbar.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }else{
                    updateUser(
                        USER!!.uid,
                        userProfileEditBinding.uName.text.toString(),
                        userProfileEditBinding.uGender.text.toString(),
                        userProfileEditBinding.uAge.text.toString(),
                        userProfileEditBinding.uAddress.text.toString(),
                        uLat,
                        uLong,
                        USER!!.uEmail,
                        userProfileEditBinding.ccp.fullNumberWithPlus,
                        USER!!.uProfilePic,
                        true,
                        true
                    )
                }
            }


        }
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        userProfileEditBinding.uAge.setOnClickListener {
            val datePicker = DatePickerDialog(this, {
                    _, selectedYear, selectedMonth, selectedDay ->

                Log.d("@DOB", DOB)
            },year,month,day)
            datePicker.setTitle("Select your Date of Birth")
            datePicker.show()
        }

        userProfileEditBinding.uGender.setOnClickListener {
            val menu = PopupMenu(this@UserProfileEdit,it )
            menu.menu.add("Male") // menus items
            menu.menu.add("Female") // menus items
            menu.menu.add("Don't want to share")
            menu.setOnMenuItemClickListener {
                gender = it.title.toString()
                userProfileEditBinding.uGender.setText(it.title)
                true
            }
            menu.show()
        }
        userProfileEditBinding.picBtn.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                val alert = MaterialAlertDialogBuilder(this).also {
                    it.setTitle("Permission Required")
                    it.setMessage("We need stroage permission to access your photos.\nGo to setting then permission and grant photos permission")
                    it.setPositiveButton("setting"){
                        dialog, which ->
                            openAppSettings(this@UserProfileEdit)
                            dialog.dismiss()
                    }
                    it.setNegativeButton("no"){
                        dialog, which ->
                            dialog.dismiss()
                    }
                }
                alert.show()
            }else{
                val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickImageIntent.type = "image/*"
                startActivityForResult(pickImageIntent, 100)
            }


        }
        userProfileEditBinding.fetch.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if (checkGPS()){
                    fetchLocation()
                }else{
                    buildAlertMessageNoGps()
                }

            }else{
                val alert = MaterialAlertDialogBuilder(this@UserProfileEdit)
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
            val alert = AlertDialog.Builder(this@UserProfileEdit)
            alert.setMessage("Location permission is required for fetching your location")
            alert.setPositiveButton("Allow Permission") {
                    dialog,_ ->
                com.example.doctorapp.VIEW.openAppSettings(this)
            }
            alert.setNegativeButton("Cancel") {
                    dialog,_ ->
                dialog.dismiss()
            }
            alert.show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            // Copy image to app's private storage
            val savedPath = copyUriToInternalFile(this, uri)
            if (savedPath != null) {
                Log.d("@save", "onActivityResult: $savedPath")
                USER!!.uProfilePic = savedPath
                Glide.with(this)
                    .load(USER!!.uProfilePic)
                    .into(userProfileEditBinding.profilePic)
            } else {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
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
    fun copyUriToInternalFile(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun updateUser(
        uid: String,
        uName: String,
        uGender: String,
        DOB: String,
        uAddress: String,
        lat: Float,
        long: Float,
        uEmail: String,
        uPhone: String,
        uProfilePic: String,
        isPhoneVerified: Boolean,
        isDetailFilled: Boolean
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
            val result = viewmodel.updateUser(user = USER!!)
            if (result.isSuccessful){
                val intent = Intent(this@UserProfileEdit, Splash::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }else{
                Snackbar.make(userProfileEditBinding.main,result.message(),Snackbar.LENGTH_SHORT).show()
            }

        }
    }
    private fun checkGPS(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
