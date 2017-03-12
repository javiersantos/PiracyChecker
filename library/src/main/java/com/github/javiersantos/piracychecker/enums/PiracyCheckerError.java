package com.github.javiersantos.piracychecker.enums;

public enum PiracyCheckerError {
    NOT_LICENSED("This user is not using a licensed application from Google Play."),
    SIGNATURE_NOT_VALID("This app is using another signature. The original APK has been modified."),
    INVALID_INSTALLER_ID("This app has been installed from a non-allowed source."),
    USING_DEBUG_APP("This is a debug build."),
    USING_APP_IN_EMULATOR("This app is being used in an emulator."),
    PIRATE_APP_INSTALLED("At least one pirate app has been detected on device."),
    THIRD_PARTY_STORE_INSTALLED("At least one third-party store has been detected on device."),
    // Other errors
    INVALID_PACKAGE_NAME("Application package name is invalid."),
    NON_MATCHING_UID("Application UID doesn\'t match."),
    NOT_MARKET_MANAGED("Not market managed error."),
    CHECK_IN_PROGRESS("License check is in progress."),
    INVALID_PUBLIC_KEY("Application public key is invalid."),
    MISSING_PERMISSION("Application misses the \'com.android.vending.CHECK_LICENSE\' " +
            "permission."),
    UNKNOWN("Unknown error.");

    private final String text;

    private PiracyCheckerError(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

}