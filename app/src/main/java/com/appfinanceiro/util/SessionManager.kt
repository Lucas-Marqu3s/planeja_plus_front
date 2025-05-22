package com.appfinanceiro.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    companion object {
        const val PREF_NAME = "app_financeiro_prefs"
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
    }
    
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }
    
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
    
    fun saveUserInfo(id: Long, name: String, email: String) {
        val editor = prefs.edit()
        editor.putLong(USER_ID, id)
        editor.putString(USER_NAME, name)
        editor.putString(USER_EMAIL, email)
        editor.apply()
    }
    
    fun getUserId(): Long {
        return prefs.getLong(USER_ID, -1)
    }
    
    fun getUserName(): String? {
        return prefs.getString(USER_NAME, null)
    }
    
    fun getUserEmail(): String? {
        return prefs.getString(USER_EMAIL, null)
    }
    
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
    
    fun isLoggedIn(): Boolean {
        return fetchAuthToken() != null
    }
}
