package com.github.javiersantos.piracychecker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.github.javiersantos.licensing.AESObfuscator
import com.github.javiersantos.licensing.LibraryChecker
import com.github.javiersantos.licensing.LibraryCheckerCallback
import com.github.javiersantos.licensing.ServerManagedPolicy
import com.github.javiersantos.piracychecker.activities.LicenseActivity
import com.github.javiersantos.piracychecker.callbacks.AllowCallback
import com.github.javiersantos.piracychecker.callbacks.DoNotAllowCallback
import com.github.javiersantos.piracychecker.callbacks.OnErrorCallback
import com.github.javiersantos.piracychecker.callbacks.PiracyCheckerCallback
import com.github.javiersantos.piracychecker.enums.AppType
import com.github.javiersantos.piracychecker.enums.Display
import com.github.javiersantos.piracychecker.enums.InstallerID
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError
import com.github.javiersantos.piracychecker.enums.PirateApp
import com.github.javiersantos.piracychecker.utils.SaltUtils
import com.github.javiersantos.piracychecker.utils.getPirateApp
import com.github.javiersantos.piracychecker.utils.isDebug
import com.github.javiersantos.piracychecker.utils.isInEmulator
import com.github.javiersantos.piracychecker.utils.verifyInstallerId
import com.github.javiersantos.piracychecker.utils.verifySigningCertificates
import java.util.ArrayList
import java.util.Arrays

// Library configuration/customizations
@Suppress("unused")
@SuppressLint("HardwareIds")
class PiracyChecker(
    private var context: Context?,
    var unlicensedDialogTitle: String? =
        context?.getString(R.string.app_unlicensed).orEmpty(),
    var unlicensedDialogDescription: String? =
        context?.getString(R.string.app_unlicensed_description).orEmpty()
                   ) {
    
    private var display: Display? = null
    
    @ColorRes
    private var colorPrimary: Int = 0
    
    @ColorRes
    private var colorPrimaryDark: Int = 0
    private var withLightStatusBar: Boolean = false
    
    @LayoutRes
    private var layoutXML = -1
    private var enableLVL: Boolean = false
    private var enableSigningCertificate: Boolean = false
    private var enableUnauthorizedAppsCheck: Boolean = false
    private var enableStoresCheck: Boolean = false
    private var enableEmulatorCheck: Boolean = false
    private var enableDeepEmulatorCheck: Boolean = false
    private var enableDebugCheck: Boolean = false
    private var enableFoldersCheck: Boolean = false
    private var enableAPKCheck: Boolean = false
    private var saveToSharedPreferences: Boolean = false
    private var blockUnauthorized: Boolean = false
    private var preferences: SharedPreferences? = null
    private var preferenceSaveResult: String? = null
    private var preferenceBlockUnauthorized: String? = null
    private var licenseBase64: String? = null
    private var signatures: Array<String> = arrayOf()
    private val installerIDs: MutableList<InstallerID>
    private val extraApps: ArrayList<PirateApp>
    
    private var allowCallback: AllowCallback? = null
    private var doNotAllowCallback: DoNotAllowCallback? = null
    private var onErrorCallback: OnErrorCallback? = null
    
    // LVL
    private var libraryLVLChecker: LibraryChecker? = null
    
    // Dialog
    private var dialog: PiracyCheckerDialog? = null
    
    init {
        this.display = Display.DIALOG
        this.installerIDs = ArrayList()
        this.extraApps = ArrayList()
        this.colorPrimary = R.color.colorPrimary
        this.colorPrimaryDark = R.color.colorPrimaryDark
    }
    
    constructor(context: Context?) :
        this(
            context, context?.getString(R.string.app_unlicensed).orEmpty(),
            context?.getString(R.string.app_unlicensed_description).orEmpty())
    
    constructor(context: Context?, title: String?) :
        this(
            context, title.orEmpty(),
            context?.getString(R.string.app_unlicensed_description).orEmpty())
    
    constructor(context: Context?, @StringRes title: Int) :
        this(
            context,
            if (title != 0) context?.getString(title).orEmpty() else "")
    
    constructor(context: Context?, @StringRes title: Int, @StringRes description: Int) :
        this(
            context,
            if (title != 0) context?.getString(title).orEmpty() else "",
            if (description != 0) context?.getString(description).orEmpty() else "")
    
    fun enableGooglePlayLicensing(licenseKeyBase64: String): PiracyChecker {
        this.enableLVL = true
        this.licenseBase64 = licenseKeyBase64
        return this
    }
    
    @Deprecated(
        "Deprecated in favor of enableSigningCertificates so you can check for multiple signatures",
        ReplaceWith("enableSigningCertificates(signature)"))
    fun enableSigningCertificate(signature: String): PiracyChecker {
        this.enableSigningCertificate = true
        this.signatures = arrayOf(signature)
        return this
    }
    
    fun enableSigningCertificates(vararg signatures: String): PiracyChecker {
        this.enableSigningCertificate = true
        this.signatures = arrayOf(*signatures)
        return this
    }
    
    fun enableSigningCertificates(signatures: List<String>): PiracyChecker {
        this.enableSigningCertificate = true
        this.signatures = signatures.toTypedArray()
        return this
    }
    
    fun enableInstallerId(vararg installerID: InstallerID): PiracyChecker {
        this.installerIDs.addAll(listOf(*installerID))
        return this
    }
    
    fun enableUnauthorizedAppsCheck(): PiracyChecker {
        this.enableUnauthorizedAppsCheck = true
        return this
    }
    
    fun blockIfUnauthorizedAppUninstalled(
        preferences: SharedPreferences,
        preferenceName: String
                                         ): PiracyChecker {
        this.blockUnauthorized = true
        this.preferenceBlockUnauthorized = preferenceName
        saveToSharedPreferences(preferences)
        return this
    }
    
    fun blockIfUnauthorizedAppUninstalled(
        preferencesName: String,
        preferenceName: String
                                         ): PiracyChecker {
        this.blockUnauthorized = true
        this.preferenceBlockUnauthorized = preferenceName
        saveToSharedPreferences(preferencesName)
        return this
    }
    
    fun enableStoresCheck(): PiracyChecker {
        this.enableStoresCheck = true
        return this
    }
    
    fun enableDebugCheck(): PiracyChecker {
        this.enableDebugCheck = true
        return this
    }
    
    fun enableAPKCheck(): PiracyChecker {
        this.enableAPKCheck = true
        return this
    }
    
    fun enableEmulatorCheck(deepCheck: Boolean): PiracyChecker {
        this.enableEmulatorCheck = true
        this.enableDeepEmulatorCheck = deepCheck
        return this
    }
    
    fun enableFoldersCheck(): PiracyChecker {
        this.enableFoldersCheck = true
        return this
    }
    
    fun addAppToCheck(vararg apps: PirateApp): PiracyChecker {
        this.extraApps.addAll(Arrays.asList(*apps))
        return this
    }
    
    fun addAppToCheck(app: PirateApp): PiracyChecker {
        this.extraApps.add(app)
        return this
    }
    
    fun saveResultToSharedPreferences(
        preferences: SharedPreferences,
        preferenceName: String
                                     ): PiracyChecker {
        this.saveToSharedPreferences = true
        this.preferenceSaveResult = preferenceName
        saveToSharedPreferences(preferences)
        return this
    }
    
    fun saveResultToSharedPreferences(
        preferencesName: String,
        preferenceName: String
                                     ): PiracyChecker {
        this.saveToSharedPreferences = true
        this.preferenceSaveResult = preferenceName
        saveToSharedPreferences(preferencesName)
        return this
    }
    
    private fun saveToSharedPreferences(preferences: SharedPreferences?) {
        if (preferences != null) {
            this.preferences = preferences
        } else {
            try {
                this.preferences = (context as? Activity)?.getPreferences(Context.MODE_PRIVATE)
            } catch (e: Exception) {
                this.preferences =
                    context?.getSharedPreferences(LIBRARY_PREFERENCES_NAME, Context.MODE_PRIVATE)
            }
        }
    }
    
    private fun saveToSharedPreferences(preferencesName: String?) {
        if (preferencesName != null) {
            this.preferences = context?.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        } else {
            try {
                this.preferences = (context as? Activity)?.getPreferences(Context.MODE_PRIVATE)
            } catch (e: Exception) {
                this.preferences =
                    context?.getSharedPreferences(LIBRARY_PREFERENCES_NAME, Context.MODE_PRIVATE)
            }
        }
    }
    
    fun display(display: Display): PiracyChecker {
        this.display = display
        return this
    }
    
    fun withActivityColors(
        @ColorRes colorPrimary: Int,
        @ColorRes colorPrimaryDark: Int,
        withLightStatusBar: Boolean
                          ): PiracyChecker {
        this.colorPrimary = colorPrimary
        this.colorPrimaryDark = colorPrimaryDark
        this.withLightStatusBar = withLightStatusBar
        return this
    }
    
    fun withActivityLayout(@LayoutRes layout: Int): PiracyChecker {
        this.layoutXML = layout
        return this
    }
    
    fun allowCallback(allowCallback: AllowCallback): PiracyChecker {
        this.allowCallback = allowCallback
        return this
    }
    
    fun doNotAllowCallback(doNotAllowCallback: DoNotAllowCallback): PiracyChecker {
        this.doNotAllowCallback = doNotAllowCallback
        return this
    }
    
    fun onErrorCallback(errorCallback: OnErrorCallback): PiracyChecker {
        this.onErrorCallback = errorCallback
        return this
    }
    
    fun callback(callback: PiracyCheckerCallback): PiracyChecker {
        this.allowCallback = object : AllowCallback {
            override fun allow() {
                callback.allow()
            }
        }
        this.doNotAllowCallback = object : DoNotAllowCallback {
            override fun doNotAllow(error: PiracyCheckerError, app: PirateApp?) {
                callback.doNotAllow(error, app)
            }
        }
        this.onErrorCallback = object : OnErrorCallback {
            override fun onError(error: PiracyCheckerError) {
                super.onError(error)
                callback.onError(error)
            }
        }
        return this
    }
    
    fun destroy() {
        dismissDialog()
        destroyLVLChecker()
        context = null
    }
    
    fun start() {
        if (allowCallback == null && doNotAllowCallback == null) {
            callback(object : PiracyCheckerCallback() {
                override fun allow() {}
                
                override fun doNotAllow(error: PiracyCheckerError, app: PirateApp?) {
                    if (context is Activity && (context as Activity).isFinishing) {
                        return
                    }
                    
                    val dialogContent = when {
                        app != null ->
                            context?.getString(R.string.unauthorized_app_found, app.name).orEmpty()
                        error == PiracyCheckerError.BLOCK_PIRATE_APP ->
                            context?.getString(R.string.unauthorized_app_blocked).orEmpty()
                        else -> unlicensedDialogDescription
                    }
                    
                    if (display == Display.DIALOG) {
                        dismissDialog()
                        dialog = PiracyCheckerDialog.newInstance(
                            unlicensedDialogTitle.orEmpty(), dialogContent.orEmpty())
                        
                        context?.let {
                            dialog?.show(it) ?: {
                                Log.e(
                                    "PiracyChecker",
                                    "Unlicensed dialog was not built properly. Make sure your context is an instance of Activity")
                            }()
                        }
                    } else {
                        val intent = Intent(context, LicenseActivity::class.java)
                            .putExtra("content", dialogContent)
                            .putExtra("colorPrimary", colorPrimary)
                            .putExtra("colorPrimaryDark", colorPrimaryDark)
                            .putExtra("withLightStatusBar", withLightStatusBar)
                            .putExtra("layoutXML", layoutXML)
                        context?.startActivity(intent)
                        (context as? Activity)?.finish()
                        destroy()
                    }
                }
            })
        }
        verify()
    }
    
    private fun verify() {
        // Library will check first the non-LVL methods since LVL is asynchronous and could take
        // some seconds to give a result
        if (!verifySigningCertificate()) {
            doNotAllowCallback?.doNotAllow(PiracyCheckerError.SIGNATURE_NOT_VALID, null)
        } else if (!verifyInstallerId()) {
            doNotAllowCallback?.doNotAllow(PiracyCheckerError.INVALID_INSTALLER_ID, null)
        } else if (!verifyUnauthorizedApp()) {
            doNotAllowCallback?.doNotAllow(PiracyCheckerError.BLOCK_PIRATE_APP, null)
        } else {
            if (enableLVL) {
                val deviceId =
                    Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
                destroyLVLChecker()
                libraryLVLChecker =
                    LibraryChecker(
                        context,
                        ServerManagedPolicy(
                            context,
                            AESObfuscator(
                                SaltUtils.getSalt(context), context?.packageName, deviceId)),
                        licenseBase64)
                libraryLVLChecker?.checkAccess(object : LibraryCheckerCallback {
                    override fun allow(reason: Int) {
                        doExtraVerification(true)
                    }
                    
                    override fun dontAllow(reason: Int) {
                        doExtraVerification(false)
                    }
                    
                    override fun applicationError(errorCode: Int) {
                        onErrorCallback?.onError(
                            PiracyCheckerError.getCheckerErrorFromCode(errorCode))
                    }
                })
            } else {
                doExtraVerification(true)
            }
        }
    }
    
    private fun verifySigningCertificate(): Boolean {
        return !enableSigningCertificate || (context?.verifySigningCertificates(signatures) == true)
    }
    
    private fun verifyInstallerId(): Boolean {
        return installerIDs.isEmpty() || (context?.verifyInstallerId(installerIDs) == true)
    }
    
    private fun verifyUnauthorizedApp(): Boolean {
        return !blockUnauthorized ||
            !(preferences?.getBoolean(preferenceBlockUnauthorized, false) ?: false)
    }
    
    private fun doExtraVerification(
        possibleSuccess: Boolean
                                   ) {
        val app = context?.getPirateApp(
            enableUnauthorizedAppsCheck, enableStoresCheck, enableFoldersCheck, enableAPKCheck,
            extraApps)
        if (possibleSuccess) {
            if (enableDebugCheck && (context?.isDebug() == true)) {
                if (saveToSharedPreferences)
                    preferences?.edit()?.putBoolean(preferenceSaveResult, false)?.apply()
                doNotAllowCallback?.doNotAllow(PiracyCheckerError.USING_DEBUG_APP, null)
            } else if (enableEmulatorCheck && isInEmulator(enableDeepEmulatorCheck)) {
                if (saveToSharedPreferences)
                    preferences?.edit()?.putBoolean(preferenceSaveResult, false)?.apply()
                doNotAllowCallback?.doNotAllow(PiracyCheckerError.USING_APP_IN_EMULATOR, null)
            } else if (app != null) {
                if (saveToSharedPreferences)
                    preferences?.edit()?.putBoolean(preferenceSaveResult, false)?.apply()
                if (blockUnauthorized && app.type == AppType.PIRATE)
                    preferences?.edit()?.putBoolean(preferenceBlockUnauthorized, true)?.apply()
                doNotAllowCallback?.doNotAllow(
                    if (app.type == AppType.STORE)
                        PiracyCheckerError.THIRD_PARTY_STORE_INSTALLED
                    else
                        PiracyCheckerError.PIRATE_APP_INSTALLED, app)
            } else {
                if (saveToSharedPreferences)
                    preferences?.edit()?.putBoolean(preferenceSaveResult, true)?.apply()
                allowCallback?.allow()
            }
        } else {
            if (app != null) {
                if (saveToSharedPreferences)
                    preferences?.edit()?.putBoolean(preferenceSaveResult, false)?.apply()
                if (blockUnauthorized && app.type == AppType.PIRATE)
                    preferences?.edit()?.putBoolean(preferenceBlockUnauthorized, true)?.apply()
                doNotAllowCallback?.doNotAllow(
                    if (app.type == AppType.STORE)
                        PiracyCheckerError.THIRD_PARTY_STORE_INSTALLED
                    else
                        PiracyCheckerError.PIRATE_APP_INSTALLED, app)
            } else {
                if (saveToSharedPreferences)
                    preferences?.edit()?.putBoolean(preferenceSaveResult, false)?.apply()
                doNotAllowCallback?.doNotAllow(PiracyCheckerError.NOT_LICENSED, null)
            }
        }
    }
    
    private fun dismissDialog() {
        dialog?.dismiss()
        dialog = null
    }
    
    private fun destroyLVLChecker() {
        libraryLVLChecker?.finishAllChecks()
        libraryLVLChecker?.onDestroy()
        libraryLVLChecker = null
    }
    
    companion object {
        private const val LIBRARY_PREFERENCES_NAME = "license_check"
    }
}