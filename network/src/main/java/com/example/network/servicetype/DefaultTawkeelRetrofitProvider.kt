package com.example.network.servicetype

import com.example.network.GsonHelper
import com.example.network.interceptor.TawkeelApiHeaderInterceptor
import com.example.network.providers.AppLocaleProvider
import com.example.network.providers.OKHttpClientProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class DefaultTawkeelRetrofitProvider @Inject constructor(
    private val appLocaleProvider: AppLocaleProvider,
    private val okHttpClientProvider: OKHttpClientProvider,
    private val tawkeelApiHeaderInterceptor: TawkeelApiHeaderInterceptor
) : TawkeelRetrofitProvider {

    override fun get(): Retrofit {
        val okHttpBuilder = okHttpClientProvider.get()

        return Retrofit.Builder()
            .baseUrl("""${serviceType.baseURL}${appLocaleProvider.getLocaleWithCountryCode()}/""")
            .client(
                okHttpBuilder
                    .addInterceptor(tawkeelApiHeaderInterceptor)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create(GsonHelper.gsonIdentity))
            .build()
    }
}
