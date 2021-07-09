package com.battery.library.util

interface IBatteryState {
    fun onBatteryChanged()

    fun onBatteryOkay()

    fun onBatteryLow()

    fun onPowerConnected()

    fun onPowerDisconnected()
}