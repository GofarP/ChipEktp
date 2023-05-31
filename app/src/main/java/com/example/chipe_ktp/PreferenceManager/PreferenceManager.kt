package com.example.chipe_ktp.PreferenceManager

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {

    private lateinit var sharedPreference: SharedPreferences

    private val PREFERENCE_NAME="E-KTP_PREFERENCE"


    fun preferenceManager(context:Context)
    {
        sharedPreference=context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }


    fun putString(key:String, value:String )
    {
        val editor=sharedPreference.edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun getString(key:String): String? {
        return sharedPreference.getString(key,null)
    }

    fun clear()
    {
        val preference=sharedPreference.edit()
        preference.clear()
        preference.apply()
    }
}