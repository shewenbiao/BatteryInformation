package com.example.batteryinfomation

import android.content.Intent
import android.graphics.Color
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.battery.library.BatteryApp
import com.battery.library.BatteryConstants
import com.battery.library.data.BatteryInfo
import com.battery.library.util.BatteryUtil
import com.battery.library.util.PermissionChecker
import com.battery.library.util.SystemSettingUtil
import com.battery.library.viewmodel.BatteryViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var remainingHourTv: TextView
    private lateinit var remainingMinuteTv: TextView
    private lateinit var consumeAppsTv: TextView
    private lateinit var batteryPercentTv: TextView
    private lateinit var chargeStatusTv: TextView
    private lateinit var chargingTypeTv: TextView
    private lateinit var batteryTemperatureTv: TextView
    private lateinit var batteryVoltageTv: TextView
    private lateinit var batteryTotalCapacityTv: TextView
    private lateinit var batteryCurrentCapacityTv: TextView
    private lateinit var batteryHealthTv: TextView
    private lateinit var batteryTypeTv: TextView
    private lateinit var lastChargeSpeedTv: TextView
    private lateinit var lastChargeTypeTv: TextView
    private lateinit var lastChargeDurationTv: TextView
    private lateinit var lastChargeQuantityTv: TextView

    private lateinit var hapticFeedbackSwitch: SwitchMaterial
    private lateinit var soundEffectsSwitch: SwitchMaterial
    private lateinit var screenBrightnessSlider: Slider
    private lateinit var screenOffTimeoutSpinner: AppCompatSpinner

    private lateinit var appUsageBtn: MaterialButton

    private var batteryTotalCapacity: Double = 0.0

    private lateinit var viewModel: BatteryViewModel

    private var requestFrom = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val batteryStatus: Intent? = registerReceiver(null,
//            IntentFilter(ACTION_BATTERY_CHANGED).apply {
//                addAction(ACTION_DOCK_EVENT)
//            }
//        )
//
//        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
//        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
//                || status == BatteryManager.BATTERY_STATUS_FULL
//
//        // How are we charging?
//        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
//        val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
//        val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
//        val wirelessCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS
//        val batteryPercent: Float? = batteryStatus?.let { intent ->
//            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
//            Log.d("xxxxx", "level:$level")
//            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
//            Log.d("xxxxx", "scale:$scale")
//            level * 100 / scale.toFloat()
//        }
//
//        val dockState: Int = batteryStatus?.getIntExtra(EXTRA_DOCK_STATE, EXTRA_DOCK_STATE_UNDOCKED)
//            ?: EXTRA_DOCK_STATE_UNDOCKED
//        val isDocked: Boolean = dockState != EXTRA_DOCK_STATE_UNDOCKED
//        val isCar: Boolean = dockState == EXTRA_DOCK_STATE_CAR
//        val isDesk: Boolean = dockState == EXTRA_DOCK_STATE_DESK
//                || dockState == EXTRA_DOCK_STATE_LE_DESK
//                || dockState == EXTRA_DOCK_STATE_HE_DESK
//
        remainingHourTv = findViewById(R.id.tv_battery_hour_value)
        remainingMinuteTv = findViewById(R.id.tv_battery_minutes_value)
        consumeAppsTv = findViewById(R.id.tv_consume_app)
        batteryPercentTv = findViewById(R.id.battery_percent)
        chargeStatusTv = findViewById(R.id.charging)
        chargingTypeTv = findViewById(R.id.charging_type)
        batteryTemperatureTv = findViewById(R.id.temperature)
        batteryVoltageTv = findViewById(R.id.voltage)
        batteryTotalCapacityTv = findViewById(R.id.total_capacity)
        batteryCurrentCapacityTv = findViewById(R.id.current_capacity)
        batteryHealthTv = findViewById(R.id.health)
        batteryTypeTv = findViewById(R.id.battery_type)
        lastChargeSpeedTv = findViewById(R.id.last_charge_speed)
        lastChargeTypeTv = findViewById(R.id.last_charge_type)
        lastChargeDurationTv = findViewById(R.id.last_charge_duration)
        lastChargeQuantityTv = findViewById(R.id.last_charge_quantity)

        val activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (SystemSettingUtil.canWrite(this)) {
                        when (requestFrom) {
                            REQUEST_FROM_HAPTIC_FEEDBACK -> {
                                hapticFeedbackSwitch.isChecked = !hapticFeedbackSwitch.isChecked
                                viewModel.setHapticFeedbackEnabled(hapticFeedbackSwitch.isChecked)
                            }
                            REQUEST_FROM_SOUND_EFFECTS -> {
                                soundEffectsSwitch.isChecked = !soundEffectsSwitch.isChecked
                                viewModel.setSoundEffectsEnabled(soundEffectsSwitch.isChecked)
                            }
                            REQUEST_FROM_SCREEN_BRIGHTNESS -> {
//                            viewModel.setBrightness(screenBrightnessSlider.value / screenBrightnessSlider.valueTo)
                            }
                            REQUEST_FROM_SCREEN_OFF_TIMEOUT -> {

                            }
                        }

                    }
                }
                when (requestFrom) {
                    REQUEST_FROM_USAGE_ACCESS -> {
                        if (PermissionChecker.hasUsageAccessPermission(this)) {
                            startActivity(Intent(this, AppUsageActivity::class.java))
                        }
                    }
                }
                requestFrom = REQUEST_FROM_NONE
            }

        hapticFeedbackSwitch = findViewById(R.id.switch_haptic_feedback)
        hapticFeedbackSwitch.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !SystemSettingUtil.canWrite(this)) {
                hapticFeedbackSwitch.isChecked = !hapticFeedbackSwitch.isChecked
                requestFrom = REQUEST_FROM_HAPTIC_FEEDBACK
                activityLauncher.launch(SystemSettingUtil.getManageWriteSettingsIntent(packageName))
            } else {
                viewModel.setHapticFeedbackEnabled(hapticFeedbackSwitch.isChecked)
            }
        }

        soundEffectsSwitch = findViewById(R.id.switch_sound_effects)
        soundEffectsSwitch.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !SystemSettingUtil.canWrite(this)) {
                soundEffectsSwitch.isChecked = !soundEffectsSwitch.isChecked
                requestFrom = REQUEST_FROM_SOUND_EFFECTS
                activityLauncher.launch(SystemSettingUtil.getManageWriteSettingsIntent(packageName))
            } else {
                viewModel.setSoundEffectsEnabled(soundEffectsSwitch.isChecked)
            }
        }

        screenBrightnessSlider = findViewById(R.id.slider_brightness)
        screenBrightnessSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !SystemSettingUtil.canWrite(
                        this@MainActivity
                    )
                ) {
                    activityLauncher.launch(
                        SystemSettingUtil.getManageWriteSettingsIntent(
                            packageName
                        )
                    )

                } else {
                    val brightness = slider.value / slider.valueTo
//                        val attributes = window.attributes
//                        attributes.screenBrightness = brightness
//                        window.attributes = attributes
                    viewModel.setBrightness(brightness)
                }
            }

            override fun onStopTrackingTouch(slider: Slider) {

            }

        })
        screenOffTimeoutSpinner = findViewById(R.id.spinner_screen_off_timeout)
        screenOffTimeoutSpinner.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !SystemSettingUtil.canWrite(this@MainActivity)) {
                        activityLauncher.launch(SystemSettingUtil.getManageWriteSettingsIntent(packageName))
                    }
                }

            }
            return@setOnTouchListener false
        }
        val timeoutValues: IntArray = resources.getIntArray(R.array.screen_off_timeout_value)
        screenOffTimeoutSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val value = timeoutValues[position]
                    viewModel.setScreenOffTimeout(value * 1000)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

        appUsageBtn = findViewById(R.id.btn_app_usage)
        appUsageBtn.setOnClickListener {
            if (!PermissionChecker.hasUsageAccessPermission(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    requestFrom = REQUEST_FROM_USAGE_ACCESS
                    activityLauncher.launch(PermissionChecker.getUsageAccessSettingIntent())
                }
            } else {
                startActivity(Intent(this, AppUsageActivity::class.java))
            }
        }

//        val dockTv: TextView = findViewById(R.id.is_dock)
//        val dockStateTv: TextView = findViewById(R.id.dock_state)
//        chargeStatusTv.text = String.format(getString(R.string.is_charging), isCharging)
//        usbChargeTv.text = String.format(getString(R.string.usb_charge), usbCharge)
//        acChargeTv.text = String.format(getString(R.string.ac_charge), acCharge)
//        wirelessChargeTv.text = String.format(getString(R.string.wireless_charge), wirelessCharge)
//        batteryPercentTv.text = String.format(getString(R.string.battery_percent), batteryPercent)
//        dockTv.text = String.format(getString(R.string.is_dock), isDocked)
//        when {
//            isCar -> {
//                dockStateTv.text = String.format(getString(R.string.dock_state), "Car")
//            }
//            isDesk -> {
//                dockStateTv.text = String.format(getString(R.string.dock_state), "Desk")
//            }
//            else -> {
//                dockStateTv.text = String.format(getString(R.string.dock_state), "unknown")
//            }
//        }
//        Log.d("xxxxx", "isCharging:$isCharging")
//        Log.d("xxxxx", "usbCharge:$usbCharge")
//        Log.d("xxxxx", "acCharge:$acCharge")
//        Log.d("xxxxx", "batteryPercent:$batteryPercent")
//        Log.d("xxxxx", "isDocked:$isDocked")
//        Log.d("xxxxx", "dockState:$dockState")

        batteryTotalCapacity = BatteryUtil.getBatteryTotalCapacity(this)
        batteryTotalCapacityTv.text = String.format(
            getString(R.string.total_capacity),
            batteryTotalCapacity.toInt().toString() + "mAh"
        )

        viewModel = BatteryApp.getBatteryViewModel();
        viewModel
            .getBatteryInfo()
            .observe(this, {
                updateUI(it)
            })
        viewModel.getBatteryState().observe(this, {
            Log.d("xxxxx", "Battery Sate:${it}")
        })

        viewModel.getConsumeBatteryAppCount().observe(this, {
            val str = String.format(getString(R.string.consuming_power_des), it)
            val spannableStringBuilder = SpannableStringBuilder(str)
            val start = str.indexOf(it.toString())
            val end = start + it.toString().length
            spannableStringBuilder.setSpan(
                ForegroundColorSpan(Color.RED),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            consumeAppsTv.text = spannableStringBuilder
        })
        viewModel.getConsumeBatteryApps()

        viewModel.isHapticFeedbackEnabled().observe(this, {
            hapticFeedbackSwitch.isChecked = it
        })

        viewModel.isSoundEffectsEnabled().observe(this, {
            soundEffectsSwitch.isChecked = it
        })

        viewModel.getBrightnessValue().observe(this, {
            screenBrightnessSlider.value =
                it / 255f * (screenBrightnessSlider.valueTo - screenBrightnessSlider.valueFrom)
        })

        viewModel.getScreenOffTimeoutValue().observe(this, {
            val value = it / 1000
            if (value in timeoutValues) {
                screenOffTimeoutSpinner.setSelection(timeoutValues.indexOf(value), true)
            } else {
                screenOffTimeoutSpinner.setSelection(0, true)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getHapticFeedbackEnabled()
        viewModel.getSoundEffectsEnabled()
        viewModel.getBrightness()
        viewModel.getScreenOffTimeout()
    }

    private fun updateUI(batteryInfo: BatteryInfo) {
        val timeArray = getRemainingTimeArray(batteryInfo.remainingTime)
        remainingHourTv.text = timeArray[0]
        remainingMinuteTv.text = formatRemainingMinuteValue(timeArray[1])
        chargeStatusTv.text = String.format(getString(R.string.is_charging), batteryInfo.isCharging)
        chargingTypeTv.text = String.format(
            getString(R.string.charging_type), when {
                batteryInfo.usbCharging -> {
                    "USB"
                }
                batteryInfo.acCharging -> {
                    "AC charger"
                }
                batteryInfo.wirelessCharging -> {
                    "Wireless"
                }
                else -> {
                    "--"
                }
            }
        )
        lastChargeSpeedTv.text = String.format(
            getString(R.string.last_charge_speed), when (batteryInfo.lastChargeSpeed) {
                BatteryConstants.CHARGING_NORMAL -> {
                    "Normal"
                }
                BatteryConstants.CHARGING_FAST -> {
                    "Fast charge"
                }
                BatteryConstants.CHARGING_OVER -> {
                    "Overcharge"
                }
                else -> {
                    "--"
                }
            }
        )
        lastChargeTypeTv.text =
            String.format(
                getString(R.string.last_charge_type), when (batteryInfo.lastChargePlugged) {
                    BatteryManager.BATTERY_PLUGGED_USB -> {
                        getString(R.string.battery_plugged_usb)
                    }
                    BatteryManager.BATTERY_PLUGGED_AC -> {
                        getString(R.string.battery_plugged_ac)
                    }
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> {
                        getString(R.string.battery_plugged_wireless)
                    }
                    else -> {
                        "--"
                    }
                }
            )
        lastChargeDurationTv.text = String.format(
            getString(R.string.last_charge_duration),
            getChargeDuration(batteryInfo.lastChargeDuration)
        )
        lastChargeQuantityTv.text = String.format(
            getString(R.string.last_charge_quantity),
            batteryInfo.chargingLevel * 100 / batteryInfo.scale,
            batteryInfo.disChargingLevel * 100 / batteryInfo.scale
        )
        batteryPercentTv.text =
            String.format(
                getString(R.string.battery_percent),
                batteryInfo.batteryPercent
            )
        batteryTemperatureTv.text = String.format(
            getString(R.string.battery_temperature),
            batteryInfo.temperature.toString() + "℃"
        )
        batteryVoltageTv.text = String.format(
            getString(R.string.battery_voltage),
            (batteryInfo.voltage / 1000f).toString() + "V"
        )
        batteryTypeTv.text = String.format(getString(R.string.battery_type), batteryInfo.technology)
        batteryCurrentCapacityTv.text = String.format(
            getString(R.string.current_capacity),
            ((batteryTotalCapacity * batteryInfo.batteryPercent) / 100).toInt()
                .toString() + "mAh"
        )

        batteryHealthTv.text = String.format(
            getString(R.string.battery_health), when (batteryInfo.health) {
                BatteryManager.BATTERY_HEALTH_UNKNOWN -> {
                    getString(R.string.battery_health_unknown)
                }
                BatteryManager.BATTERY_HEALTH_GOOD -> {
                    getString(R.string.battery_health_good)
                }
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> {
                    getString(R.string.battery_health_overheat)
                }
                BatteryManager.BATTERY_HEALTH_DEAD -> {
                    getString(R.string.battery_health_dead)
                }
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> {
                    getString(R.string.battery_health_over_voltage)
                }
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> {
                    getString(R.string.battery_health_unspecified_failure)
                }
                BatteryManager.BATTERY_HEALTH_COLD -> {
                    getString(R.string.battery_health_cold)
                }
                else -> {
                    getString(R.string.battery_health_unknown)
                }
            }
        )
    }

    private fun getChargeDuration(duration: Long): String {
        if (duration <= 0) {
            return "--"
        }
        val hour = (duration / BatteryConstants.ONE_HOUR).toInt()
        val minute = (duration % BatteryConstants.ONE_HOUR / BatteryConstants.ONE_MINUTE).toInt()
        var second =
            (duration % (BatteryConstants.ONE_HOUR * BatteryConstants.ONE_MINUTE) / BatteryConstants.ONE_SECOND).toInt()
        if (second < 1) {
            second = 1
        }
        return when {
            hour > 0 -> {
                String.format(
                    getString(R.string.duration_time_h_min),
                    hour, minute
                )
            }
            minute > 0 -> {
                String.format(
                    getString(R.string.duration_time_min),
                    minute
                )
            }
            else -> {
                String.format(
                    getString(R.string.duration_time_second),
                    second
                )
            }
        }
    }

    private fun getRemainingTimeArray(time: Long): Array<String> {
        return arrayOf(
            (time / BatteryConstants.ONE_HOUR).toString(),
            (time % BatteryConstants.ONE_HOUR / BatteryConstants.ONE_MINUTE).toString()
        )
    }

    private fun formatRemainingMinuteValue(minuteValue: String): String {
        return if (minuteValue.length >= 2) {
            minuteValue
        } else "0$minuteValue"
    }

    companion object {
        const val REQUEST_FROM_NONE = -1
        const val REQUEST_FROM_HAPTIC_FEEDBACK = 1
        const val REQUEST_FROM_SOUND_EFFECTS = 2
        const val REQUEST_FROM_SCREEN_BRIGHTNESS = 3
        const val REQUEST_FROM_SCREEN_OFF_TIMEOUT = 4
        const val REQUEST_FROM_USAGE_ACCESS = 5
    }
}