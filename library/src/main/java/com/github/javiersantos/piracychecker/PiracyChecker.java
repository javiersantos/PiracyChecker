package com.github.javiersantos.piracychecker;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.support.annotation.StringRes;

import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("HardwareIds")
public class PiracyChecker {

    protected Context context;
    protected String unlicensedDialogTitle;
    protected String unlicensedDialogDescription;
    protected boolean enableLVL;
    protected boolean enableSigningCertificate;
    protected boolean enableInstallerId;
    protected String licenseBase64;
    protected String signature;
    protected List<InstallerID> installerIDs;
    protected PiracyCheckerCallback callback;

    public PiracyChecker(Context context) {
        this.context = context;
        this.unlicensedDialogTitle = context.getString(R.string.app_unlicensed);
        this.unlicensedDialogDescription = context.getString(R.string.app_unlicensed_description);
        this.installerIDs = new ArrayList<>();
    }

    public PiracyChecker(Context context, String title, String description) {
        this.context = context;
        this.unlicensedDialogTitle = title;
        this.unlicensedDialogDescription = description;
        this.installerIDs = new ArrayList<>();
    }

    public PiracyChecker(Context context, @StringRes int title, @StringRes int description) {
        this(context, context.getString(title), context.getString(description));
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
        this.installerIDs.add(installerID);
        return this;
    }

    public PiracyChecker callback(PiracyCheckerCallback callback) {
        this.callback = callback;
        return this;
    }

    public void start() {
        if (callback != null) {
            verify(callback);
        } else {
            this.callback = new PiracyCheckerCallback() {
                @Override
                public void allow() {
                }

                @Override
                public void dontAllow(PiracyCheckerError error) {
                    LibraryUtils.buildUnlicensedDialog(context, unlicensedDialogTitle,
                            unlicensedDialogDescription).show();
                }
            };
            verify(callback);
        }
    }

    protected void verify(final PiracyCheckerCallback verifyCallback) {
        // Library will verify first the non-LVL methods since LVL is asynchronous and could take
        // some seconds to give a result
        if (!verifySigningCertificate()) {
            verifyCallback.dontAllow(PiracyCheckerError.SIGNATURE_NOT_VALID);
        } else if (!verifyInstallerId()) {
            verifyCallback.dontAllow(PiracyCheckerError.INVALID_INSTALLER_ID);
        } else {
            if (enableLVL) {
                String deviceId = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                LicenseChecker licenseChecker = new LicenseChecker(context, new
                        ServerManagedPolicy(context, new AESObfuscator(LibraryUtils.SALT, context
                        .getPackageName(), deviceId)), licenseBase64);
                licenseChecker.checkAccess(new LicenseCheckerCallback() {
                    @Override
                    public void allow(int reason) {
                        verifyCallback.allow();
                    }

                    @Override
                    public void dontAllow(int reason) {
                        verifyCallback.dontAllow(PiracyCheckerError.NOT_LICENSED);
                    }

                    @Override
                    public void applicationError(int errorCode) {
                        verifyCallback.onError(PiracyCheckerUtils.getCheckerErrorFromCode
                                (errorCode));
                    }
                });
            } else {
                verifyCallback.allow();
            }
        }
    }

    protected boolean verifySigningCertificate() {
        if (enableSigningCertificate) {
            if (LibraryUtils.verifySigningCertificate(context, signature)) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    protected boolean verifyInstallerId() {
        if (enableInstallerId) {
            if (LibraryUtils.verifyInstallerId(context, installerIDs)) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

}
