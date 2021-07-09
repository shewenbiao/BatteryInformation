package com.battery.library.data

import android.os.BatteryManager

/**
 * @author : She Wenbiao
 * @date   : 2021/7/2 2:26 下午
 */

data class BatteryInfo(var status: Int) {
    var chargePlugged: Int = 0 // 充电方式 power sources: USB port, AC Charger, Wireless
    var level: Int = 0 // 当前电池电量 current battery level, from 0 to scale
    var scale: Int = 0 // 最大电池电量 the maximum battery level, 一般都是100
    var batteryPercent: Int = 0 //电量百分比 level * 100 / scale
    var temperature: Int = 0 //温度（单位：℃（摄氏度）） the current battery temperature
    var voltage: Int = 0 //电压(单位:mV(毫伏))： the current battery voltage level
    var health: Int = 0 //健康状态 the current health constant
    var technology: String = "" //电池类型 the technology of the current battery
    var lastChargeSpeed: Int = 0 // 最后一次充电状态 NORMAL, FAST, OVER
    var lastChargePlugged: Int = 0 //最后一次充电方式 : USB port, AC charger, Wireless
    var normalCharge: Int = 0 //正常充电次数
    var fastCharge: Int = 0 //快充次数
    var overCharge: Int = 0 //过充次数
    var disChargingLevel = 0 //断开充电时的电量
        get(){
            if (field == 0) {
                return level
            }
            return field
        }
    var chargingLevel: Int = 0 // 刚充电时的电量
    var lastChargeDuration: Long = 0L //最后一次充电时长(单位：ms(毫秒）
    var remainingTime: Long = 0L //电量剩余可用时长(单位：ms(毫秒）

    //是否正在充电
    val isCharging: Boolean
        get() =
            status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

    //是否正在使用usb端口充电
    val usbCharging: Boolean
        get() = chargePlugged == BatteryManager.BATTERY_PLUGGED_USB

    //是否正在使用交流充电器充电
    val acCharging: Boolean
        get() = chargePlugged == BatteryManager.BATTERY_PLUGGED_AC

    //是否正在使用无线充电
    val wirelessCharging
        get() = chargePlugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
}
