package com.github.javiersantos.piracychecker;

import android.content.Context;

import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;

public class PiracyCheckerUtils {

    public static String getAPKSignature(Context context) {
        return LibraryUtils.getCurrentSignature(context);
    }

    public static PiracyCheckerError getCheckerErrorFromCode(int errorCode) {
        switch (errorCode) {
            case 1:
                return PiracyCheckerError.INVALID_PACKAGE_NAME;
            case 2:
                return PiracyCheckerError.NON_MATCHING_UID;
            case 3:
                return PiracyCheckerError.NOT_MARKET_MANAGED;
            case 4:
                return PiracyCheckerError.CHECK_IN_PROGRESS;
            case 5:
                return PiracyCheckerError.INVALID_PUBLIC_KEY;
            case 6:
                return PiracyCheckerError.MISSING_PERMISSION;
            default:
                return PiracyCheckerError.UNKNOWN;
        }
    }

}