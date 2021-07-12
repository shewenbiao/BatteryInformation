package com.battery.library.viewmodel

import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battery.library.BatteryApp
import com.battery.library.data.BatteryInfo
import com.battery.library.data.BatteryState
import com.battery.library.data.LastUsedApp
import com.battery.library.util.SystemSettingUtil
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

    private val hapticFeedbackEnabled = MutableLiveData<Boolean>()

    private val soundEffectsEnabled = MutableLiveData<Boolean>()

    private val screenBrightness = MutableLiveData<Int>()

    private val screenOffTimeout = MutableLiveData<Int>()

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
                val list =
                    TaskFetcher.getLastUsedAppList(BatteryApp.getApplication(), startTime, endTime)
                lastUsedAppList.value = list
            }
        }
    }

    fun isHapticFeedbackEnabled() = hapticFeedbackEnabled

    fun getHapticFeedbackEnabled() {
        val enable = SystemSettingUtil.isHapticFeedbackEnabled(BatteryApp.getApplication())
        hapticFeedbackEnabled.value = enable
    }

    fun setHapticFeedbackEnabled(enable: Boolean): Boolean {
        val value = if (enable) {
            1
        } else {
            0
        }
        return SystemSettingUtil.setHapticFeedback(BatteryApp.getApplication(), value)
    }

    fun isSoundEffectsEnabled() = soundEffectsEnabled

    fun getSoundEffectsEnabled() {
        val enable = SystemSettingUtil.isSoundEffectsEnabled(BatteryApp.getApplication())
        soundEffectsEnabled.value = enable
    }

    fun setSoundEffectsEnabled(enable: Boolean): Boolean {
        val value = if (enable) {
            1
        } else {
            0
        }
        return SystemSettingUtil.setSoundEffects(BatteryApp.getApplication(), value)
    }

    fun getBrightnessValue() = screenBrightness

    fun getBrightness() {
        val value = SystemSettingUtil.getBrightness(BatteryApp.getApplication())
        screenBrightness.value = value
    }

    fun setBrightness(brightness: Float): Boolean {
        return SystemSettingUtil.setBrightness(BatteryApp.getApplication(), brightness)
    }

    fun getScreenOffTimeoutValue() = screenOffTimeout

    fun getScreenOffTimeout() {
        val value = SystemSettingUtil.getScreenOffTimeout(BatteryApp.getApplication())
        screenOffTimeout.value = value
    }

    fun setScreenOffTimeout(timeout: Int): Boolean {
        return SystemSettingUtil.setScreenOffTimeout(BatteryApp.getApplication(), timeout)
    }

}