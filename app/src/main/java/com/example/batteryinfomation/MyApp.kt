package com.example.batteryinfomation

import android.app.Application
import com.battery.library.BatteryApp
import com.battery.library.util.BatteryListenerHelper
import com.battery.library.util.IBatteryState

/**
 * @author : She Wenbiao
 * @date   : 2021/7/2 12:20 下午
 */
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        BatteryApp.init(this)

        val batteryListener = BatteryListenerHelper(this)
        batteryListener.register(object : IBatteryState {
            override fun onBatteryChanged() {

            }

            override fun onBatteryLow() {

            }

            override fun onBatteryOkay() {

            }

            override fun onPowerConnected() {

            }

            override fun onPowerDisconnected() {

            }

        })
    }
}