package com.battery.library.data

import android.graphics.drawable.Drawable

/**
 * @author : She Wenbiao
 * @date   : 2021/7/9 2:57 下午
 */
data class InstalledAppInfo(val packageName: String) {
    var appName = ""
    var icon: Drawable? = null
}
