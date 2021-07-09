package com.battery.library.viewmodel

import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battery.library.BatteryApp
import com.battery.library.data.BatteryInfo
import com.battery.library.data.BatteryState
import com.battery.library.data.LastUsedApp
import com.battery.library.util.TaskFetcher
import kotlinx.coroutines.launch

/**
 * @author : She Wenbiao
 * @date   : 2021/7/2 10:48 下午
 */
class BatteryViewModel : ViewModel() {

    private val batteryInfo = MutableLiveData<BatteryInfo>()

    private val batteryState = MutableLiveData<BatteryState>()

    private val consumeBatteryAppCount = MutableLiveData<Int>()

    private val lastUsedAppList = MutableLiveData<List<LastUsedApp>>()

    fun getBatteryInfo() = batteryInfo

    fun setBatteryStateInfo(batteryInfo: BatteryInfo) {
        this.batteryInfo.value = batteryInfo
    }

    fun getBatteryState() = batteryState

    fun setBatteryState(batteryState: BatteryState) {
        this.batteryState.value = batteryState
    }

    fun getConsumeBatteryAppCount() = consumeBatteryAppCount

    fun getConsumeBatteryApps() {
        viewModelScope.launch {
            val count = TaskFetcher.getCanStopAppList(BatteryApp.getApplication()).size
            consumeBatteryAppCount.value = count
        }
    }

    fun getLastUsedAppList() = lastUsedAppList

    fun getLastUsedApps(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val list = TaskFetcher.getLastUsedAppList(BatteryApp.getApplication(), startTime, endTime)
                lastUsedAppList.value = list
            }
        }
    }
}