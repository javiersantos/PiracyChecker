package com.github.javiersantos.piracychecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;

import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PirateApp;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

class LibraryUtils {
    static final byte[] SALT = new byte[]{
            -85, -55, 27, 58, -83, 27, -34, -45, 101, -98, 12, 37, 13, -17, 95, -28, -62, -32,
            -32, 33
    };

    static AlertDialog buildUnlicensedDialog(Context context, String title, String content) {
        if (!(context instanceof Activity)) return null;
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

    @SuppressLint("PackageManagerGetSignatures")
    static String getCurrentSignature(Context context) {
        String res = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
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
        final String installer = context.getPackageManager().getInstallerPackageName(
                context.getPackageName());

        for (InstallerID id : installerID) {
            validInstallers.addAll(id.toIDs());
        }

        return installer != null && validInstallers.contains(installer);
    }

    @SuppressLint("SdCardPath")
    static PirateApp getPirateApp(Context context, boolean lpf, boolean stores) {
        if (!lpf && !stores) return null;
        for (PirateApp app : getApps()) {
            if ((lpf && app.isUnauthorized()) || (stores && !app.isUnauthorized())) {
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
                        if (isIntentAvailable(context, intent)) {
                            return app;
                        }
                    } catch (Exception ignored2) {
                        try {
                            if (hasPermissions(context)) {
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
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
     * except in compliance with the License. You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software distributed under the
     * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
     * either express or implied. See the License for the specific language governing permissions
     * and limitations under the License.
     *
     * Copyright (C) 2013, Vladislav Gingo Skoumal (http://www.skoumal.net)
     */
    static boolean isInEmulator(boolean deepCheck) {
        int ratingCheckEmulator = 0;

        if (Build.PRODUCT.contains("sdk") ||
                Build.PRODUCT.contains("Andy") ||
                Build.PRODUCT.contains("ttVM_Hdragon") ||
                Build.PRODUCT.contains("google_sdk") ||
                Build.PRODUCT.contains("Droid4X") ||
                Build.PRODUCT.contains("nox") ||
                Build.PRODUCT.contains("sdk_x86") ||
                Build.PRODUCT.contains("sdk_google") ||
                Build.PRODUCT.contains("vbox86p")) {
            ratingCheckEmulator++;
        }

        if (Build.MANUFACTURER.equals("unknown") ||
                Build.MANUFACTURER.equals("Genymotion") ||
                Build.MANUFACTURER.contains("Andy") ||
                Build.MANUFACTURER.contains("MIT") ||
                Build.MANUFACTURER.contains("nox") ||
                Build.MANUFACTURER.contains("TiantianVM")) {
            ratingCheckEmulator++;
        }

        if (Build.BRAND.equals("generic") ||
                Build.BRAND.equals("generic_x86") ||
                Build.BRAND.equals("TTVM") ||
                Build.BRAND.contains("Andy")) {
            ratingCheckEmulator++;
        }

        if (Build.DEVICE.contains("generic") ||
                Build.DEVICE.contains("generic_x86") ||
                Build.DEVICE.contains("Andy") ||
                Build.DEVICE.contains("ttVM_Hdragon") ||
                Build.DEVICE.contains("Droid4X") ||
                Build.DEVICE.contains("nox") ||
                Build.DEVICE.contains("generic_x86_64") ||
                Build.DEVICE.contains("vbox86p")) {
            ratingCheckEmulator++;
        }

        if (Build.MODEL.equals("sdk") ||
                Build.MODEL.equals("google_sdk") ||
                Build.MODEL.contains("Droid4X") ||
                Build.MODEL.contains("TiantianVM") ||
                Build.MODEL.contains("Andy") ||
                Build.MODEL.equals("Android SDK built for x86_64") ||
                Build.MODEL.equals("Android SDK built for x86")) {
            ratingCheckEmulator++;
        }

        if (Build.HARDWARE.equals("goldfish") ||
                Build.HARDWARE.equals("vbox86") ||
                Build.HARDWARE.contains("nox") ||
                Build.HARDWARE.contains("ttVM_x86")) {
            ratingCheckEmulator++;
        }

        if (Build.FINGERPRINT.contains("generic") ||
                Build.FINGERPRINT.contains("generic/sdk/generic") ||
                Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") ||
                Build.FINGERPRINT.contains("Andy") ||
                Build.FINGERPRINT.contains("ttVM_Hdragon") ||
                Build.FINGERPRINT.contains("generic_x86_64") ||
                Build.FINGERPRINT.contains("generic/google_sdk/generic") ||
                Build.FINGERPRINT.contains("vbox86p") ||
                Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
            ratingCheckEmulator++;
        }

        if (deepCheck) {
            try {
                String opengl = GLES20.glGetString(GLES20.GL_RENDERER);
                if (opengl != null) {
                    if (opengl.contains("Bluestacks") || opengl.contains("Translator"))
                        ratingCheckEmulator += 10;
                }
            } catch (Exception ignored) {
            }

            try {
                File sharedFolder = new File(Environment.getExternalStorageDirectory().toString()
                        + File.separatorChar
                        + "windows"
                        + File.separatorChar
                        + "BstSharedFolder");
                if (sharedFolder.exists())
                    ratingCheckEmulator += 10;
            } catch (Exception ignored) {
            }
        }

        return ratingCheckEmulator > 3;
    }

    static boolean isDebug(Context context) {
        return ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    private static ArrayList<PirateApp> getApps() {
        ArrayList<PirateApp> apps = new ArrayList<>();
        apps.add(new PirateApp("Lucky Patcher", new String[]{"c", "o", "m", ".", "c", "h", "e",
                "l", "p", "u", "s", ".", "l", "a", "c", "k", "y", "p", "a", "t", "c", "h"}));
        apps.add(new PirateApp("Lucky Patcher", new String[]{"c", "o", "m", ".", "d", "i", "m",
                "o", "n", "v", "i", "d", "e", "o", ".", "l", "u", "c", "k", "y", "p", "a", "t",
                "c", "h", "e", "r"}));
        apps.add(new PirateApp("Lucky Patcher", new String[]{"c", "o", "m", ".", "f", "o", "r",
                "p", "d", "a", ".", "l", "p"}));
        apps.add(new PirateApp("Lucky Patcher", new String[]{"c", "o", "m", ".", "a", "n", "d",
                "r", "o", "i", "d", ".", "v", "e", "n", "d", "i", "n", "g", ".", "b", "i", "l",
                "l", "i", "n", "g", ".", "I", "n", "A", "p", "p", "B", "i", "l", "l", "i", "n",
                "g", "S", "e", "r", "v", "i", "c", "e", ".", "L", "U", "C", "K"}));
        apps.add(new PirateApp("Lucky Patcher", new String[]{"c", "o", "m", ".", "a", "n", "d",
                "r", "o", "i", "d", ".", "v", "e", "n", "d", "i", "n", "g", ".", "b", "i", "l",
                "l", "i", "n", "g", ".", "I", "n", "A", "p", "p", "B", "i", "l", "l", "i", "n",
                "g", "S", "e", "r", "v", "i", "c", "e", ".", "L", "O", "C", "K"}));
        apps.add(new PirateApp("Lucky Patcher", new String[]{"c", "o", "m", ".", "a", "n", "d",
                "r", "o", "i", "d", ".", "v", "e", "n", "d", "i", "n", "g", ".", "b", "i", "l",
                "l", "i", "n", "g", ".", "I", "n", "A", "p", "p", "B", "i", "l", "l", "i", "n",
                "g", "S", "e", "r", "v", "i", "c", "e", ".", "L", "A", "C", "K"}));
        apps.add(new PirateApp("Uret Patcher", new String[]{"u", "r", "e", "t", ".", "j", "a",
                "s", "i", "2", "1", "6", "9", ".", "p", "a", "t", "c", "h", "e", "r"}));
        apps.add(new PirateApp("Freedom", new String[]{"c", "c", ".", "m", "a", "d", "k", "i",
                "t", "e", ".", "f", "r", "e", "e", "d", "o", "m"}));
        apps.add(new PirateApp("Freedom", new String[]{"c", "c", ".", "c", "z", ".", "m", "a",
                "d", "k", "i", "t", "e", ".", "f", "r", "e", "e", "d", "o", "m"}));
        apps.add(new PirateApp("CreeHack", new String[]{"o", "r", "g", ".", "c", "r", "e", "e",
                "p", "l", "a", "y", "s", ".", "h", "a", "c", "k"}));
        apps.add(new PirateApp("Aptoide", new String[]{"c", "m", ".", "a", "p", "t", "o", "i",
                "d", "e", ".", "p", "t"}));
        apps.add(new PirateApp("BlackMart", new String[]{"o", "r", "g", ".", "b", "l", "a", "c",
                "k", "m", "a", "r", "t", ".", "m", "a", "r", "k", "e", "t"}));
        apps.add(new PirateApp("Mobogenie", new String[]{"c", "o", "m", ".", "m", "o", "b", "o",
                "g", "e", "n", "i", "e"}));
        apps.add(new PirateApp("1Mobile", new String[]{"m", "e", ".", "o", "n", "e", "m", "o",
                "b", "i", "l", "e", ".", "a", "n", "d", "r", "o", "i", "d"}));
        apps.add(new PirateApp("GetApk", new String[]{"c", "o", "m", ".", "r", "e", "p", "o",
                "d", "r", "o", "i", "d", ".", "a", "p", "p"}));
        apps.add(new PirateApp("GetJar", new String[]{"c", "o", "m", ".", "g", "e", "t", "j",
                "a", "r", ".", "r", "e", "w", "a", "r", "d", "s"}));
        apps.add(new PirateApp("SlideMe", new String[]{"c", "o", "m", ".", "s", "l", "i", "d",
                "e", "m", "e", ".", "s", "a", "m", ".", "m", "a", "n", "a", "g", "e", "r"}));
        apps.add(new PirateApp("ACMarket", new String[]{"n", "e", "t", ".", "a", "p", "p", "c",
                "a", "k", "e"}));
        return apps;
    }

    private static boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    private static boolean hasPermissions(Context context) {
        try {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ||
                    !shouldAskPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    (!(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.READ_EXTERNAL_STORAGE)));
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    private static boolean shouldAskPermission(Context context, String permission) {
        if (shouldAskPermission()) {
            int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

}
