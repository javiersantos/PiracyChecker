package com.github.javiersantos.piracychecker.callbacks

import com.github.javiersantos.piracychecker.enums.PiracyCheckerError
import com.github.javiersantos.piracychecker.enums.PirateApp

interface AllowCallback {
    /**
     * Called after the app checked as valid and licensed
     */
    fun allow()
}

interface DoNotAllowCallback {
    @Deprecated("dontAllow has been deprecated in favor of doNotAllow", ReplaceWith("doNotAllow"))
    fun dontAllow(error: PiracyCheckerError, app: PirateApp?) =
        doNotAllow(error, app)
    
    /**
     * Called if the app is not valid or the user is using an unlicensed version. Check errors at
     * [PiracyCheckerError].
     *
     * @param error
     * PiracyCheckerError.NOT_LICENSED, PiracyCheckerError.SIGNATURE_NOT_VALID or
     * PiracyCheckerError.INVALID_INSTALLER_ID
     * @param app
     * The [PirateApp] that has been detected on device. Returns null in no app was
     * found.
     */
    fun doNotAllow(error: PiracyCheckerError, app: PirateApp?)
}

interface OnErrorCallback {
    /**
     * Called if an error with the license check occurs. Check errors at [ ].
     *
     * @param error
     * PiracyCheckerError.INVALID_PACKAGE_NAME, PiracyCheckerError.NON_MATCHING_UID,
     * PiracyCheckerError.NOT_MARKET_MANAGED, PiracyCheckerError.CHECK_IN_PROGRESS,
     * PiracyCheckerError.INVALID_PUBLIC_KEY, PiracyCheckerError.MISSING_PERMISSION or
     * PiracyCheckerError.UNKNOWN
     */
    fun onError(error: PiracyCheckerError) {}
}

abstract class PiracyCheckerCallback : AllowCallback,
                                       DoNotAllowCallback,
                                       OnErrorCallback