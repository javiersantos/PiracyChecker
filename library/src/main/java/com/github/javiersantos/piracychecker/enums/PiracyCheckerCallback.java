package com.github.javiersantos.piracychecker.enums;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class PiracyCheckerCallback {

    /**
     * Called after the app checked as valid and licensed
     */
    public abstract void allow();

    /**
     * Called if the app is not valid or the user is using an unlicensed version. Check errors at
     * {@link PiracyCheckerError}.
     *
     * @param error PiracyCheckerError.NOT_LICENSED, PiracyCheckerError.SIGNATURE_NOT_VALID or
     *              PiracyCheckerError.INVALID_INSTALLER_ID
     * @param app   The {@link PirateApp} that has been detected on device. Returns null in no app
     *              was found.
     */
    public abstract void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app);


    /**
     * Called if an error with the license check occurs. Check errors at {@link
     * PiracyCheckerError}.
     *
     * @param error PiracyCheckerError.INVALID_PACKAGE_NAME, PiracyCheckerError.NON_MATCHING_UID,
     *              PiracyCheckerError.NOT_MARKET_MANAGED, PiracyCheckerError.CHECK_IN_PROGRESS,
     *              PiracyCheckerError.INVALID_PUBLIC_KEY, PiracyCheckerError.MISSING_PERMISSION or
     *              PiracyCheckerError.UNKNOWN
     */
    public void onError(@NonNull PiracyCheckerError error) {
    }

}