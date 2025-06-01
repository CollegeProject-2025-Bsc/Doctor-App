package com.example.doctorapp.UTIL

import android.content.Context
import android.widget.Toast

class mToast {

    companion object{
        fun showToast(context:Context,message:String){
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
        }

        val mToast by lazy {
            mToast()
        }
    }


}