package com.example.network.interceptor

import com.example.network.providers.ApiHeadersProvider
import com.example.network.providers.DefaultApiHeadersProvider.Companion.AUTHORIZATION
import com.example.network.providers.DefaultApiHeadersProvider.Companion.DEVICE_ID
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

interface TawkeelApiHeaderInterceptor : Interceptor

class DefaultTawkeelApiHeaderInterceptor @Inject constructor(
    private val apiHeadersProvider: ApiHeadersProvider,
) : TawkeelApiHeaderInterceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().apply {
                apiHeadersProvider.get().forEach { entry ->
                    if (entry.key != DEVICE_ID || !chain.call().request().url.toString().contains("payfort-token")) {
//                        Timber.d("${entry.key} -- ${entry.value}")
                        addHeader(entry.key, entry.value)
                    }
                }
                if (removeToken(chain))
                    removeHeader(AUTHORIZATION)

            }.build()
        )
    }

    private fun removeToken(chain: Interceptor.Chain) =
        chain.call().request().url.toString().contains("refresh-token")
}
