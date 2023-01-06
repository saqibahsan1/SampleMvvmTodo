package com.example.network

import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

suspend inline fun <T> callApi(crossinline apiFunction: suspend () -> T): ApiResult<T> =
    try {
        ApiResult.Success(apiFunction.invoke())
    } catch (exception: Exception) {
        when (exception) {
            is IOException, is UnknownHostException -> ApiResult.getNetworkFailure()
            is HttpException -> ApiResult.getFailureFromException(exception)
            else -> ApiResult.technicalFailure()
        }
    }

suspend fun <T> ApiResult<BaseResponse<T>>.unZipWithData(
    successBlock: suspend (T) -> Unit,
    failureBlock: suspend (ErrorWithCode) -> Unit
) {
    when (this) {
        is ApiResult.Failure -> failureBlock(ErrorWithCode(message = error.message, error.statusCode))
        is ApiResult.Success ->
            if (data.status) {
                data.data?.let {
                    successBlock(it)
                } ?: failureBlock(ErrorWithCode(message = data.message, data.statusCode))
            } else {
                failureBlock(ErrorWithCode(message = data.message, data.statusCode))
            }
    }
}

suspend fun <T> ApiResult<BaseResponse<T>>.unZipWithNullableData(
    successBlock: suspend (T?) -> Unit,
    failureBlock: suspend (ErrorWithCode) -> Unit
) {
    when (this) {
        is ApiResult.Failure -> failureBlock(ErrorWithCode(message = error.message, error.statusCode))
        is ApiResult.Success ->
            if (data.status) {
                successBlock(data.data)
            } else {
                failureBlock(ErrorWithCode(message = data.message, data.statusCode))
            }
    }
}

data class ErrorWithCode(
    val message: String,
    val code: Int
)
