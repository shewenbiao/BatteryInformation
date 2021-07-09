package com.battery.library.data

/**
 * @author : She Wenbiao
 * @date   : 2021/7/7 2:40 下午
 */
data class LastUsedApp(var packageName: String) {

    /**
     * the number of times the app was launched as an activity from outside of the app.
     * @hide
     */
    var launchCount: Int = 0

    /**
     * the total time this package spent in the foreground, measured in milliseconds.
     */
    var totalTimeInForeground: Long = 0L

    /**
     * the last time this package's activity was used, measured in milliseconds since the epoch.
     */
    var lastTimeUsed: Long = 0L


    override fun toString(): String {
        return "LastUsedApp(packageName='$packageName', launchCount=$launchCount, totalTimeInForeground=$totalTimeInForeground, lastTimeUsed=$lastTimeUsed)"
    }


}
