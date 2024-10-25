package dev.jescas.wificollector.main.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jescas.wificollector.main.data.WifiItem
import dev.jescas.wificollector.main.repository.WifiRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _wifiItems = MutableLiveData<List<WifiItem>>()
    val wifiItems: LiveData<List<WifiItem>> = _wifiItems
    val wifiRepository = WifiRepository(application)

    fun startWifi() {
        wifiRepository.enableWifi()
        viewModelScope.launch {
            wifiRepository.getNetworks().collect { wifiList ->
                _wifiItems.value = wifiList
            }
        }
    }

    fun triggerScanNetwork() {
        wifiRepository.scanNetworkTrigger()
    }


}