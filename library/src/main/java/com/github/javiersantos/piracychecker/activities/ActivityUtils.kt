package com.github.javiersantos.piracychecker.activities

import android.content.Context
import android.os.Build
import android.view.View

internal fun Context.getAppName(): String {
    var name: String = try {
        (packageManager?.getApplicationLabel(applicationInfo) ?: "").toString()
    } catch (e: Exception) {
        ""
    }
    if (name.isNotBlank() && name.isNotEmpty()) return name
    
    val stringRes = applicationInfo?.labelRes ?: 0
    name = if (stringRes == 0) {
        applicationInfo?.nonLocalizedLabel?.toString() ?: ""
    } else {
        try {
            getString(stringRes)
        } catch (e: Exception) {
            ""
        }
    }
    return name
}

internal fun View.setupLightStatusBar(enable: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = systemUiVisibility
        flags =
            if (enable) flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        systemUiVisibility = flags
    }
}