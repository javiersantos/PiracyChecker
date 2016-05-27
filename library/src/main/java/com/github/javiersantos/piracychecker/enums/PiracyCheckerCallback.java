package com.github.javiersantos.piracychecker.enums;

import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;

public interface PiracyCheckerCallback {

    /**
     * allow method called after the app is valid and licensed
     * dontAllow method called if the app is not valid or the user is using an unlicensed version
     */
    void allow();

    void dontAllow(PiracyCheckerError error);

}
