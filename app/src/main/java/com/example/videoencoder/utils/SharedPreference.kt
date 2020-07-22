package com.example.videoencoder.utils

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.videoencoder.application.App


object Preferences {
    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor
    private const val PREF_NAME = "WellBeing"

    init {
        sharedPreferences = App().getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
    }


    fun putString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()

    }


    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }


    fun putInteger(key: String, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }


    fun getInteger(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun putBoolean(name: String, value: Boolean) {
        editor.putBoolean(name, value)
        editor.apply()
    }

    fun getBoolean(name: String): Boolean? {
        return sharedPreferences.getBoolean(name, true)
    }

    fun getBoolean(name: String, value: Boolean): Boolean? {
        return sharedPreferences.getBoolean(name, value)
    }

    fun putFloat(name: String, value: Float) {
        editor.putFloat(name, value)
        editor.apply()
    }

    fun getFloat(name: String): Float {

        return sharedPreferences.getFloat(name, 1f)

    }

    fun putLong(name: String, value: Long) {
        editor.putLong(name, value)
        editor.apply()
    }

    fun getLong(name: String): Long {
        return sharedPreferences.getLong(name, 0)
    }

}