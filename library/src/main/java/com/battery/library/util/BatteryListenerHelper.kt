package com.battery.library.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.battery.library.receiver.BatteryBroadcastReceiver

/**
 * @author : She Wenbiao
 * @date   : 2021/7/2 12:11 下午
 */
class BatteryListenerHelper(private val context: Context) {

    private var receiver: BatteryBroadcastReceiver? = null

    private var mIBatteryState: IBatteryState? = null

    fun register(IBatteryState: IBatteryState?) {
        this.mIBatteryState = IBatteryState
        if (receiver == null) {
            receiver = BatteryBroadcastReceiver(this.mIBatteryState)
        }
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        context.registerReceiver(receiver, intentFilter)
    }

    fun unregister() {
        receiver?.let {
            context.unregisterReceiver(it)
        }
        this.mIBatteryState = null
    }
}