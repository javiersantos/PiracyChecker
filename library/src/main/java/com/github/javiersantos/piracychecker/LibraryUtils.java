package com.github.javiersantos.piracychecker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;

import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PirateApp;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("PackageManagerGetSignatures")
class LibraryUtils {
    static final byte[] SALT = new byte[]{
            -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32,
            -64, 89
    };

    static AlertDialog buildUnlicensedDialog(Context context, String title, String content) {
        final Activity activity = (Activity) context;
        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(context.getString(R.string.app_unlicensed_close), new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (activity.isFinishing())
                                    return;
                                activity.finish();
                            }
                        })
                .create();
    }

    static String getCurrentSignature(Context context) {
        String res = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context
                    .getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                res = Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {
        }
        return res.trim();
    }

    static boolean verifySigningCertificate(Context context, String appSignature) {
        return getCurrentSignature(context).equals(appSignature);
    }

    static boolean verifyInstallerId(Context context, List<InstallerID> installerID) {
        List<String> validInstallers = new ArrayList<>();
        final String installer = context.getPackageManager().getInstallerPackageName(context
                .getPackageName());

        for (InstallerID id : installerID) {
            validInstallers.addAll(id.toIDs());
        }

        return installer != null && validInstallers.contains(installer);
    }

    @SuppressLint("SdCardPath")
    static PirateApp getPirateApp(Context context, boolean lpf, boolean stores) {
        if (!lpf && !stores) return null;
        for (PirateApp app : PiracyCheckerUtils.getApps()) {
            if ((lpf && app.isLPF()) || (stores && !app.isLPF())) {
                StringBuilder builder = new StringBuilder();
                for (String s : app.getPack()) {
                    builder.append(s);
                }
                String pack = builder.toString();
                PackageManager pm = context.getPackageManager();
                try {
                    PackageInfo info = pm.getPackageInfo(pack, PackageManager.GET_META_DATA);
                    if (info != null) return app;
                } catch (PackageManager.NameNotFoundException ignored1) {
                    try {
                        Intent intent = pm.getLaunchIntentForPackage(pack);
                        if (PiracyCheckerUtils.isIntentAvailable(context, intent)) {
                            return app;
                        }
                    } catch (Exception ignored2) {
                        try {
                            if (PiracyCheckerUtils.hasPermissions(context)) {
                                File file1 = new File("/data/app/" + pack + "-1/base.apk");
                                File file2 = new File("/data/app/" + pack + "-2/base.apk");
                                File file3 = new File("/data/app/" + pack + ".apk");
                                File file4 = new File("/data/data/" + pack + ".apk");
                                File file5 = new File("/data/data/" + pack);
                                File file6 = new File(context.getFilesDir().getPath() + pack +
                                        ".apk");
                                File file7 = new File(context.getFilesDir().getPath() + pack);
                                File file8 = new File(Environment.getExternalStorageDirectory() +
                                        "/Android/data/" + pack);
                                if (file1.exists() || file2.exists() || file3.exists() ||
                                        file4.exists() || file5.exists() || file6.exists() ||
                                        file7.exists() || file8.exists()) {
                                    return app;
                                }
                            }
                        } catch (Exception ignored3) {
                        }
                    }
                }
            }
        }
        return null;
    }

    static boolean isInEmulator() {
        try {
            boolean goldfish = PiracyCheckerUtils.getSystemProperty("ro.hardware")
                    .contains("goldfish");
            boolean emu = PiracyCheckerUtils.getSystemProperty("ro.kernel.qemu").length() > 0;
            boolean sdk = PiracyCheckerUtils.getSystemProperty("ro.product.model").equals("sdk");
            if (emu || goldfish || sdk) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    static boolean isDebug(Context context) {
        return ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

}