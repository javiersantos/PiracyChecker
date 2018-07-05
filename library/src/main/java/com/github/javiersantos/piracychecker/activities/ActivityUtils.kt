package com.github.javiersantos.piracychecker.activities

import android.content.Context
import android.os.Build
import android.view.View

fun Context.getAppName(): String = getString(applicationInfo.labelRes)

fun View.setupLightStatusBar(enable: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = systemUiVisibility
        flags = if (enable)
            flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        else
            flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        systemUiVisibility = flags
    }
}