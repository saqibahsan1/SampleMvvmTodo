package com.example.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.NetworkInterface
import java.util.*
import javax.inject.Inject

interface NetworkUtils {
    fun getMacAddress(interfaceName: String? = null): String
    fun getIPAddress(): String
    fun hasInternet(): Boolean
}

class DefaultNetworkUtils @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkUtils {
    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    override fun getMacAddress(interfaceName: String?): String {
        try {
            val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                if (interfaceName != null) {
                    if (networkInterface.name == interfaceName) continue
                }
                val mac: ByteArray = networkInterface.hardwareAddress ?: return EMPTY_STRING
                val buf = StringBuilder()
                for (aMac in mac) buf.append(String.format("%02X:", aMac))
                if (buf.isNotEmpty()) buf.deleteCharAt(buf.length - 1)
                return buf.toString()
            }
        } catch (ignored: Exception) {
            // ignored
        }
        return EMPTY_STRING
    }

    /**
     * Get IP address from first non-localhost interface
     * @return address or empty string
     */
    override fun getIPAddress(): String {
        try {
            Collections.list(NetworkInterface.getNetworkInterfaces()).forEach { networkInterface ->
                Collections.list(networkInterface.inetAddresses).forEach { iNetAddress ->
                    if (!iNetAddress.isLoopbackAddress) {
                        val address: String = iNetAddress.hostAddress ?: return EMPTY_STRING
                        val isIPv4 = address.indexOf(':') < 0
                        if (isIPv4) return address else if (!isIPv4) {
                            val delimeter = address.indexOf('%')
                            return if (delimeter < 0) address.uppercase() else address.substring(0, delimeter).uppercase()
                        }
                    }
                    return EMPTY_STRING
                }
            }
        } catch (ignored: Exception) {
            // ignored
        }
        return EMPTY_STRING
    }

    override fun hasInternet(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}
