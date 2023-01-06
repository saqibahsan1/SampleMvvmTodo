package com.example.network

object ApiErrors {
    const val NOT_FOUND_ERROR_CODE = 404
    const val NOT_FOUND_ERROR_MESSAGE = "There's no source found for this end-point."

    const val UN_AUTH_ERROR_CODE = 401
    const val USER_NOT_AUTHORIZED_MESSAGE = "User unauthorized."

    const val EXCEPTION_CODE = 506
    const val EXCEPTION_MESSAGE = "Something went wrong, please try again later!"

    const val NO_NETWORK_CODE = 501
    const val NO_NETWORK_MESSAGE = "There's no internet connection!"

    const val NO_USER_FOUND_CODE = 1910
    const val NO_USER_FOUND_MESSAGE = "No User found"

    const val UN_AUTHORIZED_USER_CODE = 1500
    const val UN_AUTHORIZED_USER_MESSAGE = "Un-Authorized User"

    const val WRONG_QUIZ_CODE = 1923
    const val WRONG_QUIZ_MESSAGE = "Wrong quiz posted"


    const val DUAL_LOGIN = 1449
    const val DUAL_LOGIN_MESSAGE = "Dual Login on different devices"

    const val DEVICE_NOT_REGISTERED = 1300
    const val DEVICE_NOT_REGISTERED_MESSAGE = "DEVICE not registered"

    const val MERGING_REQUIRED = 1994
    const val MERGING_REQUIRED_MESSAGE = "user merging required in case of same email registered via mobile login"

    const val INVALID_SOCIAL_TOKEN = 1904
    const val INVALID_SOCIAL_TOKEN_MESSAGE = "Token passed is invalid"

    const val PERFORMERS_NOT_FOUND = "performers_not_found"
    const val STEP_ALREADY_ENDED = "step_already_ended"
}