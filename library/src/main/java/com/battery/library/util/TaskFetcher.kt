package com.battery.library.util

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.battery.library.BatteryApp
import com.battery.library.data.InstalledAppInfo
import com.battery.library.data.LastUsedApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author : She Wenbiao
 * @date   : 2021/7/9 12:41 下午
 */
object TaskFetcher {

    private val WHITE_LIST_PACKAGE = arrayListOf(
        "android",
        "com.google.android.gsf",
        "com.google.android.gsf.login",
        "com.android.systemui",
        "com.google.android.packageinstaller",
        "com.android.settings",
        "com.google.android.gms"
    )

    private val WHITE_LIST_KEYS =
        arrayListOf("input", "time", "clock", "provider", "system", "launcher", "package")

    suspend fun getCanStopAppList(context: Context): List<InstalledAppInfo> =
        withContext(Dispatchers.Default) {
            val list = ArrayList<InstalledAppInfo>()
            val packageManager = context.packageManager
            val packageInfoList = packageManager.getInstalledPackages(0)
            for (packageInfo in packageInfoList) {
                val packageName = packageInfo.packageName
                val applicationInfo = packageInfo.applicationInfo
                val systemApp = applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM > 0
                val appStopped = applicationInfo.flags and ApplicationInfo.FLAG_STOPPED > 0
                val appPersistent = applicationInfo.flags and ApplicationInfo.FLAG_PERSISTENT > 0
                if (systemApp || appStopped || appPersistent) {
                    continue
                }
                if (WHITE_LIST_PACKAGE.contains(packageName)
                    || BatteryApp.getApplication().packageName.equals(packageName)
                ) {
                    continue
                }
                val installedAppInfo = InstalledAppInfo(packageName).apply {
                    appName = applicationInfo.loadLabel(packageManager).toString()
                    icon = applicationInfo.loadIcon(packageManager)
                }
                list.add(installedAppInfo)
            }
            list
        }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    suspend fun getLastUsedAppList(
        context: Context,
        startTime: Long,
        endTime: Long
    ): List<LastUsedApp> = withContext(Dispatchers.Default) {
        val list = ArrayList<LastUsedApp>()
        if (!PermissionChecker.hasUsageAccessPermission(context)) {
            return@withContext list
        }
        try {
            //此处AppCompatActivity.USAGE_STATS_SERVICE提示最低版本要求是Android5.1, 但经测试5.0上也可用
            val statsManager =
                context.getSystemService(AppCompatActivity.USAGE_STATS_SERVICE) as UsageStatsManager
            val queryUsageStats: List<UsageStats> = statsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
            if (queryUsageStats.isNotEmpty()) {
                for (usageStats in queryUsageStats) {
                    val appTotalTimeInForeground = usageStats.totalTimeInForeground
                    if (appTotalTimeInForeground == 0L) {
                        continue
                    }
                    val appLaunchCount = try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            //经测试9.0及以上版本才有getAppLaunchCount()方法
                            Class
                                .forName("android.app.usage.UsageStats")
                                .getMethod("getAppLaunchCount")
                                .invoke(usageStats) as Int
                        } else {
                            usageStats::class.java.getDeclaredField("mLaunchCount")
                                .getInt(usageStats)
                        }
                    } catch (e: Exception) {
                        0
                    }
                    if (appLaunchCount == 0) {
                        continue
                    }
                    val appLastTimeUsed = usageStats.lastTimeUsed
                    val packageName = usageStats.packageName
                    val lastUsedApp = LastUsedApp(packageName)
                    if (!list.contains(lastUsedApp)) {
                        lastUsedApp.apply {
                            name = AppUtil.getApplicationName(context, packageName)
                            icon = AppUtil.getAppIcon(context, packageName)
                            launchCount = appLaunchCount
                            totalTimeInForeground = appTotalTimeInForeground
                            lastTimeUsed = appLastTimeUsed
                        }
                        list.add(lastUsedApp)
                    } else {
                        val index = list.indexOf(lastUsedApp)
                        val usedApp = list[index]
                        usedApp.launchCount += appLaunchCount
                        usedApp.totalTimeInForeground += appTotalTimeInForeground
                        if (usedApp.lastTimeUsed < appLastTimeUsed) {
                            usedApp.lastTimeUsed = appLastTimeUsed
                        }
                    }
                }
            }
        } catch (e: Exception) {

        }
        list.filter { it.name.isNotEmpty() && it.packageName != context.packageName }
    }
}