package com.example.network

import com.google.gson.Gson
import retrofit2.HttpException

data class ErrorResponse(
    val message: String,
    val statusCode: Int
)

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Failure(val error: ErrorResponse) : ApiResult<Nothing>()

    companion object {
        fun technicalFailure(): Failure =
            Failure(ErrorResponse(ApiErrors.EXCEPTION_MESSAGE, ApiErrors.EXCEPTION_CODE))

        fun getNetworkFailure(message: String = ApiErrors.NO_NETWORK_MESSAGE): Failure =
            Failure(ErrorResponse(message, ApiErrors.NO_NETWORK_CODE))

        fun getFailureFromException(exception: HttpException): Failure {
            return try {
                val errorResponseBody = exception.response()?.errorBody()
                val errorResponse =
                    Gson().fromJson(errorResponseBody?.string(), ErrorResponse::class.java)
                if (errorResponse?.message?.isNotEmpty() == true) {
                    Failure(
                        ErrorResponse(
                            message = errorResponse.message,
                            statusCode = errorResponse.statusCode
                        )
                    )
                } else technicalFailure()
            } catch (e: Exception) {
                AppLogger.e(exception = e)
                technicalFailure()
            }
        }

        fun failure(exception: java.lang.Exception) =
            Failure(
                ErrorResponse(
                    message = exception.localizedMessage ?: EMPTY_STRING,
                    ApiErrors.EXCEPTION_CODE
                )
            )
    }
}
