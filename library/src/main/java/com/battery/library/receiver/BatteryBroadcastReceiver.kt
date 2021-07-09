package com.battery.library.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import com.battery.library.BatteryApp
import com.battery.library.BatteryConstants
import com.battery.library.data.BatteryInfo
import com.battery.library.data.BatteryState
import com.battery.library.util.IBatteryState
import com.battery.library.util.BatteryStatsImpl

/**
 * @author : She Wenbiao
 * @date   : 2021/7/1 5:23 下午
 */
class BatteryBroadcastReceiver(private var IBatteryState: IBatteryState?) :
    BroadcastReceiver() {

    private var batteryInfo: BatteryInfo? = null

    private val viewModel = BatteryApp.getBatteryViewModel()

    private var chargingTime = 0L
    private var disCharging = false
    private var lastChargingWattage = 0 //最后一次充电瓦数
    private var isSaveCharging = true
    private var firstChargingTime = 0L //每次充电的第一时间
    private var chargingLevel = 0
    private var lastChargeSpeed = 0
    private var lastChargePlugged = 0
    private var isFull = false
    private var chargeTimeFull = 0L
    private var normalCharge = 0
    private var fastCharge = 0
    private var overCharge = 0

    override fun onReceive(context: Context?, intent: Intent?) {
//        Toast.makeText(context, "action:${intent?.action}", Toast.LENGTH_SHORT).show()
        when (intent?.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                viewModel.setBatteryState(BatteryState.BATTERY_CHANGED)
                IBatteryState?.onBatteryChanged()
                getBatteryData(intent)
            }
            Intent.ACTION_BATTERY_OKAY -> {
                viewModel.setBatteryState(BatteryState.BATTERY_OKAY)
                IBatteryState?.onBatteryOkay()
            }
            Intent.ACTION_BATTERY_LOW -> {
                viewModel.setBatteryState(BatteryState.BATTERY_LOW)
                IBatteryState?.onBatteryLow()
            }
            Intent.ACTION_POWER_CONNECTED -> {
                viewModel.setBatteryState(BatteryState.POWER_CONNECTED)
                IBatteryState?.onPowerConnected()
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                viewModel.setBatteryState(BatteryState.POWER_DISCONNECTED)
                IBatteryState?.onPowerDisconnected()
            }
        }
    }

    private fun getBatteryData(intent: Intent?) {
        intent?.let {
            val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val chargePlugged: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPercent = level * 100 / scale
            val temperature: Int = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10
            val voltage: Int = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            val health: Int = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            val technology: String? = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)

            val intProperty: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val batteryManager = BatteryApp.getBatteryManager()
                if (batteryManager != null) {
                    //平均电池电流*当前电压
                    batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE) * voltage
                } else {
                    0
                }
            } else {
                0
            }

            BatteryStatsImpl.getInstance().setBatteryState(status, chargePlugged, level, BatteryApp.getApplication())
            val remainingTime = BatteryStatsImpl.getInstance().computeBatteryTimeRemaining()
            if (batteryInfo == null) {
                batteryInfo = BatteryInfo(status)
            }
            batteryInfo?.apply {
                this.status = status
                this.chargePlugged = chargePlugged
                this.level = level
                this.scale = scale
                this.batteryPercent = batteryPercent
                this.temperature = temperature
                this.voltage = voltage
                this.health = health
                this.technology = technology ?: ""
                this.remainingTime = remainingTime
                viewModel.setBatteryStateInfo(batteryInfo!!)
            }

            when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> {
                    chargingTime = System.currentTimeMillis()
                    disCharging = false
                    if (intProperty > 0 && lastChargingWattage < intProperty) {
                        lastChargingWattage = intProperty
                    }
                    if (isSaveCharging) {
                        firstChargingTime = chargingTime
                        chargingLevel = level
                        isSaveCharging = false
                        lastChargePlugged = chargePlugged
                    }
                }
                BatteryManager.BATTERY_STATUS_DISCHARGING,
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> {
                    batteryStatusDischarging(level, scale)
                }
                BatteryManager.BATTERY_STATUS_FULL -> {
                    disCharging = false
                    if (!isFull) {
                        chargeTimeFull = System.currentTimeMillis()
                        lastChargePlugged = chargePlugged
                        isFull = true
                    }
                }
            }
        }
    }

    private fun batteryStatusDischarging(level: Int, scale: Int) {
        val currentTimeMillis = System.currentTimeMillis()
        if (!disCharging) {
            val duration = currentTimeMillis - firstChargingTime
            if (firstChargingTime != 0L) {
                batteryInfo?.apply {
                    lastChargeDuration = duration
                    viewModel.setBatteryStateInfo(batteryInfo!!)
                }
            }
            if (level == scale) {
                if (chargeTimeFull != 0L
                    && currentTimeMillis - chargeTimeFull > BatteryConstants.OVERCHARGE_TIME
                ) {
                    overCharge++
                    lastChargeSpeed = BatteryConstants.CHARGING_OVER
                    lastChargingWattage = 0
                    chargeTimeFull = 0
                    isSaveCharging = true
                    isFull = false
                    disCharging = true
                }
            }
            lastChargeSpeed = getChargingSpeed(lastChargingWattage)
            batteryInfo?.apply {
                lastChargeSpeed = this@BatteryBroadcastReceiver.lastChargeSpeed
                disChargingLevel = level
                chargingLevel = this@BatteryBroadcastReceiver.chargingLevel
                lastChargePlugged = this@BatteryBroadcastReceiver.lastChargePlugged
                normalCharge = this@BatteryBroadcastReceiver.normalCharge
                fastCharge = this@BatteryBroadcastReceiver.fastCharge
                overCharge = this@BatteryBroadcastReceiver.overCharge
                viewModel.setBatteryStateInfo(batteryInfo!!)
            }
            lastChargingWattage = 0
            chargeTimeFull = 0
            isSaveCharging = true
            isFull = false
            disCharging = true
        }
    }

    private fun getChargingSpeed(lastChargingWattage: Int): Int {
        if (lastChargingWattage < BatteryConstants.FAST_THRESHOLD) {
            normalCharge++
            return BatteryConstants.CHARGING_NORMAL
        }
        fastCharge++
        return BatteryConstants.CHARGING_FAST
    }
}