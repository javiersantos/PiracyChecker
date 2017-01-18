package com.github.javiersantos.piracychecker.enums;

public interface PiracyCheckerCallback {

    /**
     * Called after the app checked as valid and licensed
     */
    void allow();

    /**
     * Called if the app is not valid or the user is using an unlicensed version
     *
     * @param error PiracyCheckerError.NOT_LICENSED, PiracyCheckerError.SIGNATURE_NOT_VALID or
     *              PiracyCheckerError.INVALID_INSTALLER_ID
     */
    void dontAllow(PiracyCheckerError error);

}
