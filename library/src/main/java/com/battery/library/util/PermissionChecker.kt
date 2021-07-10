package com.battery.library.util

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

/**
 * @author : She Wenbiao
 * @date   : 2021/7/10 10:54 上午
 */
object PermissionChecker {

    fun hasUsageAccessPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val packageManager = context.packageManager
                val info = packageManager.getApplicationInfo(context.packageName, 0)
                val appOpsManager =
                    context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    info.uid,
                    info.packageName
                ) == AppOpsManager.MODE_ALLOWED
            } catch (e: Exception) {
                false
            }
        } else {
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun openUsageAccessSetting(activity: Activity, requestCode: Int) {
        try {
            val intent = getUsageAccessSettingIntent()
            activity.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getUsageAccessSettingIntent() = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    }
}