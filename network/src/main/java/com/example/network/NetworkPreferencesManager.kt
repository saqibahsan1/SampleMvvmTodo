package com.example.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface NetworkPreferencesManager {
    fun getAuthToken(): String
    fun setAuthToken(token: String?)
    fun removeAuthToken()
    fun setRefreshToken(token: String?)
    fun getRefreshToken():String
    fun getLocale(): String
    fun setLocale(locale: String)
}

class DefaultNetworkPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkPreferencesManager {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(
            context.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
    }

    override fun getAuthToken(): String {
        return getString(AUTH_TOKEN)
    }

    override fun setAuthToken(token: String?) {
        setString(AUTH_TOKEN, token)
    }
    override fun removeAuthToken() {
           sharedPreferences.edit().remove(AUTH_TOKEN).apply()
    }


    override fun setRefreshToken(token: String?) {
        setString(REFRESH_TOKEN, token)
    }

    override fun getRefreshToken(): String {
        return getString(REFRESH_TOKEN)
    }

    override fun getLocale(): String {
        return getString(LOCALE)
    }

    override fun setLocale(locale: String) {
        setString(LOCALE, locale)
    }

    private fun getString(key: String): String =
        sharedPreferences.getString(key, EMPTY_STRING) ?: EMPTY_STRING

    private fun setString(key: String, value: String?) =
        sharedPreferences.edit().putString(key, value).apply()

    companion object {
        private const val AUTH_TOKEN = "auth_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val LOCALE = "locale"
    }
}
