package com.github.javiersantos.piracychecker.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.opengl.GLES20
import android.os.Build
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Base64
import com.github.javiersantos.piracychecker.R
import com.github.javiersantos.piracychecker.enums.AppType
import com.github.javiersantos.piracychecker.enums.InstallerID
import com.github.javiersantos.piracychecker.enums.PirateApp
import java.io.File
import java.security.MessageDigest
import java.util.ArrayList

internal fun Context.buildUnlicensedDialog(title: String, content: String): AlertDialog? {
    return (this as? Activity)?.let {
        if (isFinishing) return null
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(title)
            .setMessage(content)
            .setPositiveButton(
                getString(R.string.app_unlicensed_close),
                DialogInterface.OnClickListener { _, _ ->
                    if (isFinishing)
                        return@OnClickListener
                    finish()
                })
            .create()
    }
}

val Context.apkSignature: String
    get() = currentSignature

val Fragment.apkSignature: String
    get() = context!!.apkSignature

val Context.currentSignature: String
    @SuppressLint("PackageManagerGetSignatures")
    get() {
        var res = ""
        try {
            val packageInfo =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in packageInfo.signatures) {
                val messageDigest = MessageDigest.getInstance("SHA")
                messageDigest.update(signature.toByteArray())
                res = Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT)
            }
        } catch (ignored: Exception) {
        }
        return res.trim()
    }

internal fun Context.verifySigningCertificate(appSignature: String?): Boolean =
    appSignature?.let { currentSignature == it } ?: false

internal fun Context.verifyInstallerId(installerID: List<InstallerID>): Boolean {
    val validInstallers = ArrayList<String>()
    val installer = packageManager.getInstallerPackageName(packageName)
    for (id in installerID) {
        validInstallers.addAll(id.toIDs())
    }
    return installer != null && validInstallers.contains(installer)
}

@SuppressLint("SdCardPath")
internal fun Context.getPirateApp(
    lpf: Boolean,
    stores: Boolean,
    folders: Boolean,
    apks: Boolean,
    extraApps: ArrayList<PirateApp>
                                 ): PirateApp? {
    if (!lpf && !stores && extraApps.isEmpty()) return null
    
    val apps = getApps(extraApps)
    var installed = false
    var theApp: PirateApp? = null
    
    try {
        val pm = packageManager
        val list = pm?.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps) {
            val checkLPF = lpf && app.type == AppType.PIRATE
            val checkStore = stores && app.type == AppType.STORE
            val checkOther = app.type == AppType.OTHER
            if (checkLPF || checkStore || checkOther) {
                installed = list?.any { it.packageName.contains(app.packageName) } ?: false
                if (!installed) {
                    installed = isIntentAvailable(pm.getLaunchIntentForPackage(app.packageName))
                }
            }
            if (installed) {
                theApp = app
                break
            }
        }
    } catch (e: Exception) {
    }
    
    if ((folders || apks) && theApp == null) {
        if (hasPermissions()) {
            var apkExist = false
            var foldersExist = false
            var containsFolder = false
            
            for (app in apps) {
                val pack = app.packageName
                try {
                    if (apks) {
                        val file1 = File("/data/app/$pack-1/base.apk")
                        val file2 = File("/data/app/$pack-2/base.apk")
                        val file3 = File("/data/app/$pack.apk")
                        val file4 = File("/data/data/$pack.apk")
                        apkExist = file1.exists() || file2.exists() ||
                            file3.exists() || file4.exists()
                    }
                    if (folders) {
                        val file5 = File("/data/data/$pack")
                        val file6 =
                            File(
                                "${Environment.getExternalStorageDirectory()}/Android/data/$pack")
                        foldersExist = file5.exists() || file6.exists()
                        
                        val appsContainer = File("/data/app/")
                        if (appsContainer.exists()) {
                            for (f in appsContainer.listFiles()) {
                                if (f.name.startsWith(pack))
                                    containsFolder = true
                            }
                        }
                    }
                } catch (e: Exception) {
                }
                if (containsFolder || apkExist || foldersExist) {
                    theApp = app
                    break
                }
            }
        }
    }
    
    return theApp
}

/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 *
 * Copyright (C) 2013, Vladislav Gingo Skoumal (http://www.skoumal.net)
 */
internal fun isInEmulator(deepCheck: Boolean): Boolean {
    var ratingCheckEmulator = 0
    
    if (Build.PRODUCT.contains("sdk") ||
        Build.PRODUCT.contains("Andy") ||
        Build.PRODUCT.contains("ttVM_Hdragon") ||
        Build.PRODUCT.contains("google_sdk") ||
        Build.PRODUCT.contains("Droid4X") ||
        Build.PRODUCT.contains("nox") ||
        Build.PRODUCT.contains("sdk_x86") ||
        Build.PRODUCT.contains("sdk_google") ||
        Build.PRODUCT.contains("vbox86p")) {
        ratingCheckEmulator++
    }
    
    if (Build.MANUFACTURER == "unknown" ||
        Build.MANUFACTURER == "Genymotion" ||
        Build.MANUFACTURER.contains("Andy") ||
        Build.MANUFACTURER.contains("MIT") ||
        Build.MANUFACTURER.contains("nox") ||
        Build.MANUFACTURER.contains("TiantianVM")) {
        ratingCheckEmulator++
    }
    
    if (Build.BRAND == "generic" ||
        Build.BRAND == "generic_x86" ||
        Build.BRAND == "TTVM" ||
        Build.BRAND.contains("Andy")) {
        ratingCheckEmulator++
    }
    
    if (Build.DEVICE.contains("generic") ||
        Build.DEVICE.contains("generic_x86") ||
        Build.DEVICE.contains("Andy") ||
        Build.DEVICE.contains("ttVM_Hdragon") ||
        Build.DEVICE.contains("Droid4X") ||
        Build.DEVICE.contains("nox") ||
        Build.DEVICE.contains("generic_x86_64") ||
        Build.DEVICE.contains("vbox86p")) {
        ratingCheckEmulator++
    }
    
    if (Build.MODEL == "sdk" ||
        Build.MODEL == "google_sdk" ||
        Build.MODEL.contains("Droid4X") ||
        Build.MODEL.contains("TiantianVM") ||
        Build.MODEL.contains("Andy") ||
        Build.MODEL == "Android SDK built for x86_64" ||
        Build.MODEL == "Android SDK built for x86") {
        ratingCheckEmulator++
    }
    
    if (Build.HARDWARE == "goldfish" ||
        Build.HARDWARE == "vbox86" ||
        Build.HARDWARE.contains("nox") ||
        Build.HARDWARE.contains("ttVM_x86")) {
        ratingCheckEmulator++
    }
    
    if (Build.FINGERPRINT.contains("generic") ||
        Build.FINGERPRINT.contains("generic/sdk/generic") ||
        Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") ||
        Build.FINGERPRINT.contains("Andy") ||
        Build.FINGERPRINT.contains("ttVM_Hdragon") ||
        Build.FINGERPRINT.contains("generic_x86_64") ||
        Build.FINGERPRINT.contains("generic/google_sdk/generic") ||
        Build.FINGERPRINT.contains("vbox86p") ||
        Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
        ratingCheckEmulator++
    }
    
    if (deepCheck) {
        try {
            val opengl = GLES20.glGetString(GLES20.GL_RENDERER)
            if (opengl != null) {
                if (opengl.contains("Bluestacks") || opengl.contains("Translator"))
                    ratingCheckEmulator += 10
            }
        } catch (ignored: Exception) {
        }
        
        try {
            val sharedFolder = File(
                "${Environment.getExternalStorageDirectory()}${File.separatorChar}windows${File.separatorChar}BstSharedFolder")
            if (sharedFolder.exists())
                ratingCheckEmulator += 10
        } catch (ignored: Exception) {
        }
    }
    
    return ratingCheckEmulator > 3
}

internal fun Context.isDebug(): Boolean =
    applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

private fun getApps(extraApps: ArrayList<PirateApp>): ArrayList<PirateApp> {
    val apps = ArrayList<PirateApp>()
    apps.add(
        PirateApp(
            "LuckyPatcher",
            arrayOf(
                "c", "o", "m", ".", "c", "h", "e", "l", "p", "u", "s", ".", "l", "a", "c", "k", "y",
                "p", "a", "t", "c", "h"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "LuckyPatcher",
            arrayOf(
                "c", "o", "m", ".", "d", "i", "m", "o", "n", "v", "i", "d", "e", "o", ".", "l", "u",
                "c", "k", "y", "p", "a", "t", "c", "h", "e", "r"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "LuckyPatcher",
            arrayOf("c", "o", "m", ".", "f", "o", "r", "p", "d", "a", ".", "l", "p"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "LuckyPatcher",
            arrayOf(
                "c", "o", "m", ".", "a", "n", "d", "r", "o", "i", "d", ".", "v", "e", "n", "d", "i",
                "n", "g", ".", "b", "i", "l", "l", "i", "n", "g", ".", "I", "n", "A", "p", "p", "B",
                "i", "l", "l", "i", "n", "g", "S", "e", "r", "v", "i", "c", "e"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "LuckyPatcher",
            arrayOf(
                "c", "o", "m", ".", "a", "n", "d", "r", "o", "i", "d", ".", "v", "e", "n", "d", "i",
                "n", "g", ".", "b", "i", "l", "l", "i", "n", "g", ".", "I", "n", "A", "p", "p", "B",
                "i", "l", "l", "i", "n", "g", "S", "o", "r", "v", "i", "c", "e"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "LuckyPatcher",
            arrayOf(
                "c", "o", "m", ".", "a", "n", "d", "r", "o", "i", "d", ".", "v", "e", "n", "d", "i",
                "n", "c"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "UretPatcher",
            arrayOf(
                "u", "r", "e", "t", ".", "j", "a", "s", "i", "2", "1", "6", "9", ".", "p", "a", "t",
                "c", "h", "e", "r"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "UretPatcher",
            arrayOf(
                "z", "o", "n", "e", ".", "j", "a", "s", "i", "2", "1", "6", "9", ".", "u", "r", "e",
                "t", "p", "a", "t", "c", "h", "e", "r"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "ActionLauncherPatcher",
            arrayOf("p", ".", "j", "a", "s", "i", "2", "1", "6", "9", ".", "a", "l", "3"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "Freedom",
            arrayOf(
                "c", "c", ".", "m", "a", "d", "k", "i", "t", "e", ".", "f", "r", "e", "e", "d", "o",
                "m"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "Freedom",
            arrayOf(
                "c", "c", ".", "c", "z", ".", "m", "a", "d", "k", "i", "t", "e", ".", "f", "r", "e",
                "e", "d", "o", "m"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "CreeHack",
            arrayOf(
                "o", "r", "g", ".", "c", "r", "e", "e", "p", "l", "a", "y", "s", ".", "h", "a", "c",
                "k"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "HappyMod",
            arrayOf("c", "o", "m", ".", "h", "a", "p", "p", "y", "m", "o", "d", ".", "a", "p", "k"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "Game Hacker",
            arrayOf(
                "o", "r", "g", ".", "s", "b", "t", "o", "o", "l", "s", ".", "g", "a", "m", "e", "h",
                "a", "c", "k"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "Game Killer Cheats",
            arrayOf(
                "c", "o", "m", ".", "z", "u", "n", "e", ".", "g", "a", "m", "e", "k", "i", "l", "l",
                "e", "r"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "AGK - App Killer",
            arrayOf("c", "o", "m", ".", "a", "a", "g", ".", "k", "i", "l", "l", "e", "r"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "Game Killer",
            arrayOf(
                "c", "o", "m", ".", "k", "i", "l", "l", "e", "r", "a", "p", "p", ".", "g", "a", "m",
                "e", "k", "i", "l", "l", "e", "r"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "Game Killer", arrayOf("c", "n", ".", "l", "m", ".", "s", "q"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "Game CheatIng Hacker",
            arrayOf(
                "n", "e", "t", ".", "s", "c", "h", "w", "a", "r", "z", "i", "s", ".", "g", "a", "m",
                "e", "_", "c", "i", "h"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "Game Hacker",
            arrayOf(
                "c", "o", "m", ".", "b", "a", "s", "e", "a", "p", "p", "f", "u", "l", "l", ".", "f",
                "w", "d"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "Content Guard Disabler",
            arrayOf(
                "c", "o", "m", ".", "g", "i", "t", "h", "u", "b", ".", "o", "n", "e", "m", "i", "n",
                "u", "s", "o", "n", "e", ".", "d", "i", "s", "a", "b", "l", "e", "c", "o", "n", "t",
                "e", "n", "t", "g", "u", "a", "r", "d"),
            AppType.PIRATE))
    
    apps.add(
        PirateApp(
            "Content Guard Disabler",
            arrayOf(
                "c", "o", "m", ".", "o", "n", "e", "m", "i", "n", "u", "s", "o", "n", "e", ".", "d",
                "i", "s", "a", "b", "l", "e", "c", "o", "n", "t", "e", "n", "t", "g", "u", "a", "r",
                "d"),
            AppType.PIRATE))
    apps.add(
        PirateApp(
            "Aptoide", arrayOf("c", "m", ".", "a", "p", "t", "o", "i", "d", "e", ".", "p", "t"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "BlackMart",
            arrayOf(
                "o", "r", "g", ".", "b", "l", "a", "c", "k", "m", "a", "r", "t", ".", "m", "a", "r",
                "k", "e", "t"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "BlackMart",
            arrayOf(
                "c", "o", "m", ".", "b", "l", "a", "c", "k", "m", "a", "r", "t", "a", "l", "p", "h",
                "a"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "Mobogenie",
            arrayOf("c", "o", "m", ".", "m", "o", "b", "o", "g", "e", "n", "i", "e"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "1Mobile",
            arrayOf(
                "m", "e", ".", "o", "n", "e", "m", "o", "b", "i", "l", "e", ".", "a", "n", "d", "r",
                "o", "i", "d"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "GetApk", arrayOf(
            "c", "o", "m", ".", "r", "e", "p", "o", "d", "r", "o", "i", "d", ".", "a", "p", "p"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "GetJar",
            arrayOf(
                "c", "o", "m", ".", "g", "e", "t", "j", "a", "r", ".", "r", "e", "w", "a", "r", "d",
                "s"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "SlideMe",
            arrayOf(
                "c", "o", "m", ".", "s", "l", "i", "d", "e", "m", "e", ".", "s", "a", "m", ".", "m",
                "a", "n", "a", "g", "e", "r"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "ACMarket",
            arrayOf("n", "e", "t", ".", "a", "p", "p", "c", "a", "k", "e"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "ACMarket",
            arrayOf("a", "c", ".", "m", "a", "r", "k", "e", "t", ".", "s", "t", "o", "r", "e"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "AppCake",
            arrayOf("c", "o", "m", ".", "a", "p", "p", "c", "a", "k", "e"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "Z Market",
            arrayOf("c", "o", "m", ".", "z", "m", "a", "p", "p"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "Modded Play Store",
            arrayOf(
                "c", "o", "m", ".", "d", "v", ".", "m", "a", "r", "k", "e", "t", "m", "o", "d", ".",
                "i", "n", "s", "t", "a", "l", "l", "e", "r"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "Mobilism Market",
            arrayOf(
                "o", "r", "g", ".", "m", "o", "b", "i", "l", "i", "s", "m", ".", "a", "n", "d", "r",
                "o", "i", "d"),
            AppType.STORE))
    apps.add(
        PirateApp(
            "All-in-one Downloader", arrayOf(
            "c", "o", "m", ".", "a", "l", "l", "i", "n", "o", "n", "e", ".", "f", "r", "e", "e"),
            AppType.STORE))
    apps.addAll(extraApps)
    return ArrayList(apps.distinctBy { it.packageName })
}

private fun Context.isIntentAvailable(intent: Intent?): Boolean {
    return try {
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        list?.isNotEmpty() ?: false
    } catch (e: Exception) {
        false
    }
}

private fun Context.hasPermissions(): Boolean {
    return try {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ||
            !shouldAskPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ||
            !ActivityCompat.shouldShowRequestPermissionRationale(
                this as Activity, Manifest.permission.READ_EXTERNAL_STORAGE)
    } catch (e: Exception) {
        false
    }
}

private fun shouldAskPermission(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

private fun Context.shouldAskPermission(permission: String): Boolean {
    if (shouldAskPermission()) {
        val permissionResult = ActivityCompat.checkSelfPermission(this, permission)
        return permissionResult != PackageManager.PERMISSION_GRANTED
    }
    return false
}