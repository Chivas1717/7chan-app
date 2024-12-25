package com.example.mobile.util

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "my_app_prefs"
private const val KEY_AUTH_TOKEN = "auth_token"

fun saveToken(context: Context, token: String) {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
}

fun getToken(context: Context): String? {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_AUTH_TOKEN, null)
}

fun clearToken(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().remove(KEY_AUTH_TOKEN).apply()
}
