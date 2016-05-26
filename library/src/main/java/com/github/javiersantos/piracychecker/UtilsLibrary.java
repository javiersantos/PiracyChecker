package com.github.javiersantos.piracychecker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AlertDialog;
import android.util.Base64;

import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.listeners.PiracyCheckerCallback;
import com.google.android.vending.licensing.LicenseCheckerCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class UtilsLibrary {
    static final byte[] SALT = new byte[] {
            -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32,
            -64, 89
    };

    static AlertDialog showUnlicensedDialog(Context context, String title, String content) {
        final Activity activity = (Activity) context;

        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(context.getString(R.string.app_unlicensed_close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (activity.isFinishing())
                            return;
                        activity.finish();
                    }
                }).create();
    }

    static boolean verifySigningCertificate(Context context, String appSignature) {
        boolean isValid = false;

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                final String currentSignature = Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
                if (appSignature.equals(currentSignature)) {
                    isValid = true;
                }
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {
            isValid = true;
        }

        return isValid;
    }

    static boolean verifyInstallerId(Context context, InstallerID installerID) {
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        return installer != null && installer.startsWith(installerID.toString());
    }

}
