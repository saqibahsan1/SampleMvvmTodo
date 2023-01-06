package com.example.network.providers

import android.content.Context
import android.content.res.Configuration
import android.telephony.TelephonyManager
import com.example.network.DEFAULT_COUNTRY_CODE
import com.example.network.NetworkPreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

interface AppLocaleProvider {
    fun getLocaleWithCountryCode(): String
    fun getLocaleCode(): String
    fun getArabicOrDefault(): String
    fun isArabicLocale(): Boolean
    fun isEnglishLocale(): Boolean
    fun isOtherLocale(): Boolean
    fun getCountryCode(): String
    fun isRTL(locale: Locale): Boolean
}

class DefaultAppLocaleProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkPreferencesManager: NetworkPreferencesManager
) : AppLocaleProvider {

    private fun checkIfLocaleIsRTL(locale: String) {
        if (locale.isNotEmpty() && isRTL(Locale(locale))) {
            val configuration: Configuration = context.resources.configuration
            configuration.setLayoutDirection(Locale(locale))
            context.createConfigurationContext(configuration)
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        }
    }
    override fun isRTL(locale: Locale): Boolean {
        val directionality: Int = Character.getDirectionality(locale.displayName[0]).toInt()
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT.toInt() || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.toInt()
    }

    private fun getLocale(): String {
        val locale = networkPreferencesManager.getLocale()
        checkIfLocaleIsRTL(locale)
        return locale
    }

    override fun getLocaleWithCountryCode(): String {
        val locale = getLocale()
        val localeCountryCode = context.resources.configuration.locale.country
        return if (locale.isEmpty()) """${AppLocales.English.code}-$DEFAULT_COUNTRY_CODE""" else """$locale-${context.getCountryCode()}"""
    }

    override fun getLocaleCode(): String {
        val locale = getLocale()
        return locale.ifEmpty { AppLocales.English.code }
    }

    override fun getArabicOrDefault(): String {
        return if (isArabicLocale())
            getLocale() else AppLocales.English.code
    }

    override fun isArabicLocale(): Boolean {
        return AppLocales.Arabic.code == getLocaleCode()
    }

    override fun isEnglishLocale(): Boolean {
        return AppLocales.English.code == getLocaleCode()
    }

    override fun isOtherLocale(): Boolean {
        return AppLocales.Other.code == getLocaleCode() || isEnglishLocale().not() || isArabicLocale().not()
    }

    override fun getCountryCode(): String =
        context.getCountryCode()

    private fun Context.getCountryCode(): String =
        (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).run {
            simCountryIso?.uppercase() ?: networkCountryIso?.uppercase() ?: Locale.getDefault().country
        }
}
