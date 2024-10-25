package dev.jescas.wificollector.main.data

data class WifiItem(
    val ssid: String,
    val bssid: String,
    val capabilities: String,
    val frequency: Int,
    val level: Int
)
