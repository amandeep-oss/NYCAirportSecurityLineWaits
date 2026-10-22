package ca.amandeep.nycairportsecuritylinewaits.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
fun Context.observeConnectivity() = callbackFlow {
    val connectivityManager = getSystemService<ConnectivityManager>()

    val callback = networkCallback { connectionState -> trySend(connectionState) }

    connectivityManager?.registerDefaultNetworkCallback(callback)
    connectivityManager?.getCurrentConnectivityState()?.let(this::trySend)

    awaitClose {
        connectivityManager?.unregisterNetworkCallback(callback)
    }
}

private fun networkCallback(
    callback: (ConnectionState) -> Unit,
): ConnectivityManager.NetworkCallback =
    object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(ConnectionState.Available)
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            callback(networkCapabilities.connectionState())
        }

        override fun onLost(network: Network) {
            callback(ConnectionState.Unavailable)
        }
    }

sealed class ConnectionState {
    data object Available : ConnectionState()

    data object Unavailable : ConnectionState()
}

val Context.currentConnectivityState: ConnectionState
    get() = getSystemService<ConnectivityManager>()?.getCurrentConnectivityState()
        ?: ConnectionState.Unavailable

private fun ConnectivityManager.getCurrentConnectivityState(): ConnectionState =
    getNetworkCapabilities(activeNetwork)?.connectionState() ?: ConnectionState.Unavailable

private fun NetworkCapabilities.connectionState(): ConnectionState =
    if (hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
        ConnectionState.Available
    } else {
        ConnectionState.Unavailable
    }
