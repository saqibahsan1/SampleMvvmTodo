package com.example.network.servicetype

import retrofit2.Retrofit
import javax.inject.Provider

interface RetrofitProvider<NetworkServiceType> : Provider<Retrofit> {
    val serviceType: NetworkServiceType
}

interface AppRetrofitProvider : RetrofitProvider<NetworkServiceType> {
    override val serviceType: NetworkServiceType
        get() = NetworkServiceType.Tawkeel
}
