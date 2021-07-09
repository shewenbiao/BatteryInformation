package com.battery.library

/**
 * @author : She Wenbiao
 * @date   : 2021/7/3 1:00 下午
 */
object BatteryConstants {

    const val ONE_SECOND: Long = 1000
    const val ONE_MINUTE: Long = 60 * ONE_SECOND
    const val ONE_HOUR: Long = 60 * ONE_MINUTE
    const val ONE_DAY: Long = 24 * ONE_HOUR

    const val CHARGING_NORMAL = 0
    const val CHARGING_FAST = 1
    const val CHARGING_OVER = 2
    const val FAST_THRESHOLD = 7500000  //750万瓦
    const val OVERCHARGE_TIME = 5 * ONE_MINUTE

}