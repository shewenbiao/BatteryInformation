package com.battery.library.util

import android.content.Context
import android.os.BatteryManager
import android.os.Build

/**
 * @author : She Wenbiao
 * @date   : 2021/7/1 5:29 下午
 */
object BatteryUtil {

    private const val POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile"

    /**
     * 单位：mAh (毫安)
     */
    fun getBatteryTotalCapacity(context: Context): Double {
        var batteryCapacity = 0.0
        try {
            val powerProfile = Class.forName(POWER_PROFILE_CLASS)
                .getConstructor(Context::class.java)
                .newInstance(context)
            batteryCapacity = Class
                .forName(POWER_PROFILE_CLASS)
                .getMethod("getBatteryCapacity")
                .invoke(powerProfile) as Double
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val batteryManager =
                    context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val chargeCounter =
                    batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                val capacity =
                    batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                return if (chargeCounter == Int.MIN_VALUE || capacity == Int.MIN_VALUE) 0.0 else (chargeCounter / capacity / 10.0)
            }
        }
        return batteryCapacity
    }

//    fun getBatteryTotalCapacity(context: Context): Double {
//        val mPowerProfile: Any
//        var batteryCapacity = 0.0
//
//        try {
//            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
//                .getConstructor(Context::class.java).newInstance(context)
//            batteryCapacity = Class
//                .forName(POWER_PROFILE_CLASS)
//                .getMethod("getAveragePower", String::class.java)
//                .invoke(mPowerProfile, "battery.capacity") as Double
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return batteryCapacity
//    }
}