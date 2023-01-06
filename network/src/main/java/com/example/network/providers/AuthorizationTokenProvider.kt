package com.example.network.providers

import com.example.network.NetworkPreferencesManager
import javax.inject.Inject
import javax.inject.Provider

private const val BEARER_TOKEN_TYPE = "Bearer "

interface AuthorizationTokenProvider : Provider<String>

class DefaultAuthorizationTokenProvider @Inject constructor(
    private val networkPreferencesManager: NetworkPreferencesManager
) : AuthorizationTokenProvider {

    override fun get(): String =
        """$BEARER_TOKEN_TYPE${
            networkPreferencesManager.getAuthToken()
        }"""

}