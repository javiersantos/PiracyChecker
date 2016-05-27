package com.github.javiersantos.piracychecker;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.StringRes;

import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class PiracyChecker {
    private Context context;
    private String unlicensedDialogTitle, unlicensedDialogDescription;
    private boolean enableLVL, enableSigningCertificate, enableInstallerId;
    private String licenseBase64;
    private String signature;
    private InstallerID installerID;
    private PiracyCheckerCallback callback;

    public PiracyChecker(Context context) {
        this.context = context;
        this.unlicensedDialogTitle = context.getString(R.string.app_unlicensed);
        this.unlicensedDialogDescription = context.getString(R.string.app_unlicensed_description);
    }

    public PiracyChecker(Context context, String title, String description) {
        this.context = context;
        this.unlicensedDialogTitle = title;
        this.unlicensedDialogDescription = description;
    }

    public PiracyChecker(Context context, @StringRes int title, @StringRes int description) {
        new PiracyChecker(context, context.getString(title), context.getString(description));
    }

    public PiracyChecker enableGooglePlayLicensing(String licenseKeyBase64) {
        this.enableLVL = true;
        this.licenseBase64 = licenseKeyBase64;
        return this;
    }

    public PiracyChecker enableSigningCertificate(String signature) {
        this.enableSigningCertificate = true;
        this.signature = signature;
        return this;
    }

    public PiracyChecker enableInstallerId(InstallerID installerID) {
        this.enableInstallerId = true;
        this.installerID = installerID;
        return this;
    }

    public PiracyChecker callback(PiracyCheckerCallback callback) {
        this.callback = callback;
        return this;
    }

    public void start() {
        if (callback != null)
            verify(callback);
        else
            verify(new PiracyCheckerCallback() {
                @Override
                public void allow() {}

                @Override
                public void dontAllow(PiracyCheckerError error) {
                    UtilsLibrary.showUnlicensedDialog(context, unlicensedDialogTitle, unlicensedDialogDescription).show();
                }
            });
    }

    private void verify(final PiracyCheckerCallback verifyCallback) {
       if (enableLVL) {
            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            LicenseChecker licenseChecker = new LicenseChecker(context, new ServerManagedPolicy(context, new AESObfuscator(UtilsLibrary.SALT, context.getPackageName(), deviceId)), licenseBase64);
            licenseChecker.checkAccess(new LicenseCheckerCallback() {
                @Override
                public void allow(int reason) {
                    if (verifyNonLVL(verifyCallback))
                        verifyCallback.allow();
                }

                @Override
                public void dontAllow(int reason) {
                    verifyCallback.dontAllow(PiracyCheckerError.NOT_LICENSED);
                }

                @Override
                public void applicationError(int errorCode) {}
            });
        } else {
           if (verifyNonLVL(verifyCallback))
               verifyCallback.allow();
        }
    }

    private boolean verifyNonLVL(PiracyCheckerCallback verifyCallback) {
        boolean signingVerifyValid = false;
        boolean installerIdValid = false;

        if (enableSigningCertificate) {
            if (UtilsLibrary.verifySigningCertificate(context, signature)) {
                signingVerifyValid = true;
            } else {
                verifyCallback.dontAllow(PiracyCheckerError.SIGNATURE_NOT_VALID);
            }
        } else {
            signingVerifyValid = true;
        }

        if (enableInstallerId) {
            if (UtilsLibrary.verifyInstallerId(context, installerID)) {
                installerIdValid = true;
            } else {
                verifyCallback.dontAllow(PiracyCheckerError.INVALID_INSTALLER_ID);
            }
        } else {
            installerIdValid = true;
        }


        return signingVerifyValid && installerIdValid;
    }

}
