package com.booboot.vndbandroid.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (isInitialStickyBroadcast) return
        /* Resetting the sockets everytime the connection changes, to avoid dead sockets */
        VNDBServer.closeAll()
    }

    companion object {
        fun isConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo = connectivityManager.activeNetworkInfo
            return networkInfo.state == NetworkInfo.State.CONNECTED
        }
    }
}