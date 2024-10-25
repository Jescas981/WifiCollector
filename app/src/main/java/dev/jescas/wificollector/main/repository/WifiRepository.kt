package dev.jescas.wificollector.main.repository

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import dev.jescas.wificollector.main.data.WifiItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class WifiRepository(val context: Context) {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager


    inner class WifiScanReceiver(val callback: (List<WifiItem>) -> Unit) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
            if (success) {
                if (ContextCompat.checkSelfPermission(
                        this@WifiRepository.context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val scanResults = wifiManager.scanResults
                    val wifiItems = scanResults.map { result ->
                        WifiItem(
                            result.SSID,
                            result.BSSID,
                            result.capabilities,
                            result.frequency,
                            result.level
                        )
                    }
                    // Callback here
                    callback(wifiItems)
                }
            }
        }
    }

    fun enableWifi() {
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }
    }

    fun scanNetworkTrigger() {
        wifiManager.startScan()
    }

    fun getNetworks(): Flow<List<WifiItem>> = callbackFlow {

        val broadcaster = WifiScanReceiver { wifiList ->
            trySend(wifiList)
        }

        this@WifiRepository.context.registerReceiver(
            broadcaster,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )

        awaitClose {
            this@WifiRepository.context.unregisterReceiver(broadcaster)
        }
    }
}