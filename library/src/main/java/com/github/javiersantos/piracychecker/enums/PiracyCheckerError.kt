package com.github.javiersantos.piracychecker.enums

enum class PiracyCheckerError(private val text: String) {
    NOT_LICENSED("This user is not using a licensed application from Google Play."),
    SIGNATURE_NOT_VALID("This app is using another signature. The original APK has been modified."),
    INVALID_INSTALLER_ID("This app has been installed from a non-allowed source."),
    USING_DEBUG_APP("This is a debug build."),
    USING_APP_IN_EMULATOR("This app is being used in an emulator."),
    PIRATE_APP_INSTALLED("At least one pirate app has been detected on device."),
    BLOCK_PIRATE_APP(
        "At least one pirate app has been detected and the app must be reinstalled when all " +
            "unauthorized apps are uninstalled."),
    THIRD_PARTY_STORE_INSTALLED("At least one third-party store has been detected on device."),
    
    // Other errors
    INVALID_PACKAGE_NAME("Application package name is invalid."),
    NON_MATCHING_UID("Application UID doesn\'t match."),
    NOT_MARKET_MANAGED("Not market managed error."),
    CHECK_IN_PROGRESS("License check is in progress."),
    INVALID_PUBLIC_KEY("Application public key is invalid."),
    MISSING_PERMISSION(
        "Application misses the \'com.android.vending.CHECK_LICENSE\' " + "permission."),
    UNKNOWN("Unknown error.");
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    override fun toString(): String = text
    
    companion object {
        fun getCheckerErrorFromCode(errorCode: Int): PiracyCheckerError = when (errorCode) {
            1 -> INVALID_PACKAGE_NAME
            2 -> NON_MATCHING_UID
            3 -> NOT_MARKET_MANAGED
            4 -> CHECK_IN_PROGRESS
            5 -> INVALID_PUBLIC_KEY
            6 -> MISSING_PERMISSION
            else -> UNKNOWN
        }
    }
}