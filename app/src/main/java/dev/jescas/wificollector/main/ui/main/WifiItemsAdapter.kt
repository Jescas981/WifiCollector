package dev.jescas.wificollector.main.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.jescas.wificollector.databinding.ItemWifiBinding
import dev.jescas.wificollector.main.data.WifiItem

class WifiItemsAdapter(var wifilist: List<WifiItem>) : RecyclerView.Adapter<WifiItemsAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemWifiBinding: ItemWifiBinding) :
        RecyclerView.ViewHolder(itemWifiBinding.root) {
        fun bind(wifiRSSI: WifiItem) {
            itemWifiBinding.tvSSID.text = wifiRSSI.ssid
            itemWifiBinding.tvLevel.text = "${wifiRSSI.level} dBm"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemWifiBinding =
            ItemWifiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemWifiBinding)
    }

    override fun getItemCount(): Int = wifilist.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemWifi = wifilist[position]
        holder.bind(itemWifi)
    }

}