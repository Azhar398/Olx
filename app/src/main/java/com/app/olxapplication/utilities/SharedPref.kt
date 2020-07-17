package com.app.olxapplication.utilities

import android.content.Context
import android.content.SharedPreferences
import java.security.AccessControlContext

class SharedPref (context: Context) {
    var sharedPref: SharedPreferences
    init {
        sharedPref = context.getSharedPreferences(Constants.SharedPrefName,0 )
    }

    fun setString(Key: String,value:String){
        sharedPref.edit().putString(Key,value).commit()
    }

    fun getString(Key: String): String? {
        return sharedPref.getString(Key, "")
    }


}