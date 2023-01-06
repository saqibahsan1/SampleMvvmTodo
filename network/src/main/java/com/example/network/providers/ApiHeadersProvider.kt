package com.example.network.providers

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

interface ApiHeadersProvider : Provider<HashMap<String, String>>

class DefaultApiHeadersProvider @Inject constructor(
    private val localeProvider: AppLocaleProvider,
    private val authorizationTokenProvider: AuthorizationTokenProvider,
    @ApplicationContext private val context: Context
) : ApiHeadersProvider {

    companion object {

        const val CONTENT_TYPE_KEY = "Content-Type"
        const val CONTENT_TYPE_VALUE = "application/json"
        const val AUTHORIZATION = "Authorization"
        const val LOCALE = "locale"
        const val DEVICE_ID = "deviceId"
        const val PLATFORM = "platform"

        const val ANDROID = "android"
    }

    @SuppressLint("HardwareIds")
    override fun get(): HashMap<String, String> {
        return hashMapOf<String, String>().apply {
            set(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE)
            set(AUTHORIZATION, authorizationTokenProvider.get())
            set(LOCALE, localeProvider.getLocaleWithCountryCode())
            set(DEVICE_ID, Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
            set(PLATFORM, ANDROID)
        }
    }
}
