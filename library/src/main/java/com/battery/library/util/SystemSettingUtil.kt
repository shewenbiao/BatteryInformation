package com.battery.library.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import androidx.annotation.RequiresApi

/**
 * @author : She Wenbiao
 * @date   : 2021/7/9 7:14 下午
 */
object SystemSettingUtil {

    /**
     * @return the timeout of screen off
     */
    fun getScreenOffTimeout(context: Context) =
        Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)

    /**
     * @param timeout (unit: millisecond)
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun setScreenOffTimeout(context: Context, timeout: Int): Boolean {
        if (!canWrite(context)) {
            return false
        }
        return Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT,
            timeout
        )
    }

    /**
     * Whether haptic feedback (Vibrate on tap) is enabled. The value is
     * boolean (1 or 0).
     */
    fun isHapticFeedbackEnabled(context: Context) =
        Settings.System.getInt(
            context.contentResolver,
            Settings.System.HAPTIC_FEEDBACK_ENABLED
        ) == 1

    /**
     * @param enable 1 or 0 (1:enable 0:disable)
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun setHapticFeedback(context: Context, enable: Int): Boolean {
        if (!canWrite(context)) {
            return false
        }
        return Settings.System.putInt(
            context.contentResolver,
            Settings.System.HAPTIC_FEEDBACK_ENABLED,
            enable
        )
    }

    /**
     * Whether the sounds effects (key clicks, lid open ...) are enabled. The value is
     * boolean (1 or 0)
     */
    fun isSoundEffectsEnabled(context: Context) =
        Settings.System.getInt(context.contentResolver, Settings.System.SOUND_EFFECTS_ENABLED) == 1

    /**
     * @param enable 1 or 0 (1:enable 0:disable)
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun setSoundEffects(context: Context, enable: Int): Boolean {
        if (!canWrite(context)) {
            return false
        }
        return Settings.System.putInt(
            context.contentResolver,
            Settings.System.SOUND_EFFECTS_ENABLED,
            enable
        )
    }

    /**
     * @return The screen backlight brightness between 0 and 255
     */
    fun getBrightness(context: Context) =
        Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)

    /**
     * @param brightness The screen backlight brightness between 0 and 255
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun setBrightness(context: Context, brightness: Int): Boolean {
        if (!canWrite(context)) {
            return false
        }
        return Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            brightness
        )
    }

    /**
     * Need permission: android.permission.WRITE_SETTINGS
     * @param brightness The screen backlight brightness between 0 and 1
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun setBrightness(context: Context, brightness: Float): Boolean {
        return setBrightness(context, (brightness * 255).toInt())
    }

    /**
     * Whether the automatic brightness mode is enabled.
     */
    fun isBrightnessModeAuto(context: Context): Boolean {
        return try {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            ) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } catch (e: SettingNotFoundException) {
            false
        }
    }

    /**
     * @param mode {android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
     * or android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL}
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun setBrightnessMode(context: Context, mode: Int): Boolean {
        if (!canWrite(context)) {
            return false
        }
        return Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            mode
        )
    }

    /**
     * Checks if the specified app can modify system settings. As of API
     * level 23, an app cannot modify system settings unless it declares the
     * {@link android.Manifest.permission#WRITE_SETTINGS}
     * permission in its manifest, <em>and</em> the user specifically grants
     * the app this capability. To prompt the user to grant this approval,
     * the app must send an intent with the action {@link
     * android.provider.Settings#ACTION_MANAGE_WRITE_SETTINGS}, which causes
     * the system to display a permission management screen.
     *
     * @param context App context.
     * @return true if the calling app can write to system settings, false otherwise
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun canWrite(context: Context): Boolean = Settings.System.canWrite(context)


    @RequiresApi(Build.VERSION_CODES.M)
    fun openManageWriteSettings(activity: Activity, packageName: String, requestCode: Int) {
        activity.startActivityForResult(getManageWriteSettingsIntent(packageName), requestCode)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getManageWriteSettingsIntent(packageName: String) =
        Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
}