package com.example.mobile.util

import android.content.Context
import android.content.SharedPreferences

private const val PREF_NAME = "app_prefs"
private const val KEY_TOKEN = "auth_token"
private const val KEY_USER_ID = "user_id"

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        return if (id != -1) id else null
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
