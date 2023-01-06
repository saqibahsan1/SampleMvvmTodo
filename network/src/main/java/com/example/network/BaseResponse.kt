package com.example.network

import java.io.Serializable

abstract class BaseResponse<T> : Serializable {
    abstract val data: T?
    abstract val status: Boolean
    abstract val statusCode: Int
    abstract val message: String
}
