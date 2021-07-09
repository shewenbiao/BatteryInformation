package com.battery.library

import android.app.Application
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import com.battery.library.viewmodel.BatteryViewModel

/**
 * @author : She Wenbiao
 * @date   : 2021/7/3 11:56 上午
 */
object BatteryApp {

    private lateinit var application: Application

    private lateinit var batteryViewModel: BatteryViewModel

    private lateinit var batteryManager: BatteryManager

    fun init(application: Application) {
        this.application = application
        batteryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(BatteryViewModel::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            batteryManager = application.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        }
    }

    fun getApplication() = application

    fun getBatteryViewModel() = batteryViewModel

    fun getBatteryManager(): BatteryManager? = if (this::batteryManager.isInitialized) {
        batteryManager
    } else {
        null
    }
}