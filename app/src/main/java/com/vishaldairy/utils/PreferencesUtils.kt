package com.vishaldairy.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.SharedPreferences

import com.google.gson.Gson
import com.vishaldairy.MApplication
import java.lang.reflect.Type


class PreferencesUtils(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
        editor.commit()
    }

    companion object {
        val PREFS_NAME = "com.vishal_dairy.config.file.private"


        private val prefs: SharedPreferences?
            get() = MApplication.getContext()?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        private fun getEditor(): SharedPreferences.Editor? {
            return prefs?.edit()
        }

        fun putString(key: String, value: String) {
            getEditor()?.putString(key, value)?.commit()
        }

        fun getString(key: String): String? {
            return prefs?.getString(key, "").toString()
        }

        fun putInteger(key: String, value: Int) {
            getEditor()?.putInt(key, value)?.commit()
        }

        fun getInteger(key: String): Int? {
            return prefs?.getInt(key, 0)
        }

        fun putBoolean(key: String, value: Boolean) {
            getEditor()?.putBoolean(key, value)?.commit()
        }

        fun getBoolean(key: String): Boolean? {
            return prefs?.getBoolean(key, false)
        }

        fun <T>putObject(key: String, value: T) {
            getEditor()?.putString(key, Gson().toJson(value))?.commit()
        }

        fun <T>getObject(key: String,type:Type): T? {
            val string = prefs?.getString(key,"")
            return Gson().fromJson<T>(string,type)
        }

        fun putFloat(key: String, value: Float) {
            getEditor()?.putFloat(key, value)?.commit()
        }

        fun getFloat(key: String): Float? {
            return prefs?.getFloat(key, 0.0f)
        }

        fun putLong(key: String, value: Long) {
            getEditor()?.putLong(key, value)?.commit()
        }

        fun getLong(key: String): Long? {
            return prefs?.getLong(key, 0)
        }

        fun putDouble(key: String, value: Double) {
            getEditor()?.putLong(key, java.lang.Double.doubleToLongBits(value))?.commit()
        }

        fun getDouble(key: String): Double {
            return java.lang.Double.longBitsToDouble(prefs?.getLong(key, 0)!!)
        }

        fun clearValue(key: String) {
            getEditor()?.remove(key)?.commit()
        }

        fun clearSharedPrefs() {
            getEditor()?.clear()?.commit()
        }
    }


}
