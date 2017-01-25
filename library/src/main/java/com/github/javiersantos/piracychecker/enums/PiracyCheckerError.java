package com.github.javiersantos.piracychecker.enums;

public enum PiracyCheckerError {
    NOT_LICENSED("This user is not using a licensed application from Google Play."),
    SIGNATURE_NOT_VALID("This app is using another signature. The original APK has been modified."),
    INVALID_INSTALLER_ID("This app has been installed from a non-allowed source."),
    // Other errors
    INVALID_PACKAGE_NAME("Application package name is invalid."),
    NON_MATCHING_UID("Application UID doesn't match."),
    NOT_MARKET_MANAGED("Not market managed error."),
    CHECK_IN_PROGRESS("License check is in progress."),
    INVALID_PUBLIC_KEY("Application public key is invalid."),
    MISSING_PERMISSION("Application misses the 'com.android.vending.CHECK_LICENSE' " +
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