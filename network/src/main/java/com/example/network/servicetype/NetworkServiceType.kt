package com.example.network.servicetype


sealed class NetworkServiceType(val baseURL: String) {
    object Tawkeel : NetworkServiceType("APP_BASE_URL")
}
