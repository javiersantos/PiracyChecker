package com.github.javiersantos.piracychecker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.javiersantos.licensing.AESObfuscator;
import com.github.javiersantos.licensing.LibraryChecker;
import com.github.javiersantos.licensing.LibraryCheckerCallback;
import com.github.javiersantos.licensing.ServerManagedPolicy;
import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.github.javiersantos.piracychecker.enums.PirateApp;

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
    protected boolean enableLPFCheck;
    protected boolean enableStoresCheck;
    protected boolean enableEmulatorCheck;
    protected boolean enableDebugCheck;
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

    public PiracyChecker enableLPFCheck(boolean enable) {
        this.enableLPFCheck = enable;
        return this;
    }

    public PiracyChecker enableStoresCheck(boolean enable) {
        this.enableStoresCheck = enable;
        return this;
    }

    public PiracyChecker enableDebugCheck(boolean enable) {
        this.enableDebugCheck = enable;
        return this;
    }

    public PiracyChecker enableEmulatorCheck(boolean enable) {
        this.enableEmulatorCheck = enable;
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
                public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                    String dialogContent = unlicensedDialogDescription;
                    if (app != null) {
                        dialogContent = context.getString(R.string.pirate_app_found, app
                                .getName());
                    }
                    LibraryUtils.buildUnlicensedDialog(context, unlicensedDialogTitle,
                            dialogContent).show();
                }
            };
            verify(callback);
        }
    }

    protected void verify(final PiracyCheckerCallback verifyCallback) {
        // Library will check first the non-LVL methods since LVL is asynchronous and could take
        // some seconds to give a result
        if (!verifySigningCertificate()) {
            verifyCallback.dontAllow(PiracyCheckerError.SIGNATURE_NOT_VALID, null);
        } else if (!verifyInstallerId()) {
            verifyCallback.dontAllow(PiracyCheckerError.INVALID_INSTALLER_ID, null);
        } else {
            if (enableLVL) {
                String deviceId = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                LibraryChecker libraryChecker = new LibraryChecker(context, new
                        ServerManagedPolicy(context, new AESObfuscator(LibraryUtils.SALT, context
                        .getPackageName(), deviceId)), licenseBase64);
                libraryChecker.checkAccess(new LibraryCheckerCallback() {
                    @Override
                    public void allow(int reason) {
                        doExtraVerification(verifyCallback, true);
                    }

                    @Override
                    public void dontAllow(int reason) {
                        doExtraVerification(verifyCallback, false);
                    }

                    @Override
                    public void applicationError(int errorCode) {
                        // TODO: Check this, from my personal experience, the license is verified
                        // TODO: without this permission.
                        if (errorCode == ERROR_MISSING_PERMISSION) {
                            doExtraVerification(verifyCallback, true);
                        } else {
                            verifyCallback.onError(PiracyCheckerUtils.getCheckerErrorFromCode
                                    (errorCode));
                        }
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

    protected void doExtraVerification(final PiracyCheckerCallback verifyCallback, boolean
            possibleSuccess) {
        PirateApp app = LibraryUtils.getPirateApp(context, enableLPFCheck, enableStoresCheck);
        if (possibleSuccess) {
            if (enableDebugCheck && LibraryUtils.isDebug(context)) {
                verifyCallback.dontAllow(PiracyCheckerError.USING_DEBUG_APP, null);
            } else if (enableEmulatorCheck && LibraryUtils.isInEmulator()) {
                verifyCallback.dontAllow(PiracyCheckerError.USING_APP_IN_EMULATOR, null);
            } else if (app != null) {
                verifyCallback.dontAllow(app.isLPF() ? PiracyCheckerError.PIRATE_APP_INSTALLED :
                        PiracyCheckerError.THIRD_PARTY_STORE_INSTALLED, app);
            } else {
                verifyCallback.allow();
            }
        } else {
            if (app != null) {
                verifyCallback.dontAllow(app.isLPF() ? PiracyCheckerError.PIRATE_APP_INSTALLED :
                        PiracyCheckerError.THIRD_PARTY_STORE_INSTALLED, app);
            } else {
                verifyCallback.dontAllow(PiracyCheckerError.NOT_LICENSED, null);
            }
        }
    }

}