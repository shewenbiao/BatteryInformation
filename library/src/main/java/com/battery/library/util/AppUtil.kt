package com.battery.library.util

import android.content.Context
import android.graphics.drawable.Drawable

/**
 * @author : She Wenbiao
 * @date   : 2021/7/10 11:46 上午
 */
object AppUtil {

    fun getApplicationName(context: Context, packageName: String): String =
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: Exception) {
            ""
        }

    fun getAppIcon(context: Context, packageName: String): Drawable? =
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            applicationInfo.loadIcon(packageManager)
        } catch (e: Exception) {
            null
        }
}