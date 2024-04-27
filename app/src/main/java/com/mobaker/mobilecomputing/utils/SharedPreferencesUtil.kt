package com.psut.farmjo.util

import android.app.Activity
import android.content.Context
import com.google.gson.Gson

object SharedPreferencesUtil {

        const val SHARED_PREFERENCES_NAME = "users"
        fun addStringToSharedPreferences(activity: Context, key: String, value: String?) {
            val sharedPref = activity.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)?: return
            with(sharedPref.edit()) {
                putString(key, value)
                apply()
            }
        }

        fun addObjectToSharedPreferences(activity: Context, key: String, value: Any) {

            val stringVal = Gson().toJson(value)
            addStringToSharedPreferences(activity, key, stringVal)
        }
        fun getString(activity: Context, key: String): String? {
            val sharedPref = activity.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)
            return sharedPref.getString(key,"")
        }

        fun <T> getObject(activity: Context, key: String,clazz:Class<T>): T {
            val sharedPref = activity.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)
            val stringVal =   sharedPref.getString(key,"")
            return Gson().fromJson<T>(stringVal,clazz)
        }
        fun remove(activity: Activity) {
            val sharedPref = activity.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)?: return
            with(sharedPref.edit()) {
                clear()
                commit()
            }
        }
        fun contain(activity: Activity, key:String): Boolean {
            val sharedPref = activity.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return sharedPref.contains(key)
        }

}