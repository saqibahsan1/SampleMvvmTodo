package com.example.network

import com.example.network.interceptor.CustomHttpLoggingInterceptor
import com.example.network.interceptor.DefaultTawkeelApiHeaderInterceptor
import com.example.network.interceptor.HttpLoggingInterceptor
import com.example.network.interceptor.TawkeelApiHeaderInterceptor
import com.example.network.providers.*
import com.example.network.servicetype.DefaultTawkeelRetrofitProvider
import com.example.network.servicetype.TawkeelRetrofitProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkBindingModule {

    @Binds
    fun bindOKHttpClientProvider(default: DefaultOKHttpClientProvider): OKHttpClientProvider

    @Binds
    fun bindHttpLoggingInterceptor(default: CustomHttpLoggingInterceptor): HttpLoggingInterceptor

    @Binds
    fun bindTawkeelApiHeaderInterceptor(default: DefaultTawkeelApiHeaderInterceptor): TawkeelApiHeaderInterceptor

    @Binds
    fun bindTawkeelApiHeadersProvider(default: ApiHeadersProvider): ApiHeadersProvider

    @Binds
    fun bindTawkeelRetrofitProvider(default: DefaultTawkeelRetrofitProvider): TawkeelRetrofitProvider

    @Binds
    fun bindAppLocaleProvider(default: DefaultAppLocaleProvider): AppLocaleProvider

    @Binds
    @Singleton
    fun bindAuthorizationTokenProvider(default: DefaultAuthorizationTokenProvider): AuthorizationTokenProvider

    @Binds
    @Singleton
    fun bindNetworkPreferencesManager(default: DefaultNetworkPreferencesManager): NetworkPreferencesManager

    @Binds
    fun bindNetworkUtils(default: DefaultNetworkUtils): NetworkUtils
}
