package com.github.javiersantos.piracychecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.github.javiersantos.piracychecker.enums.PirateApp;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("StaticFieldLeak")
@SuppressWarnings("unchecked")
public class PiracyCheckerUtils {

    public static String getAPKSignature(Context context) {
        return LibraryUtils.getCurrentSignature(context);
    }

    static PiracyCheckerError getCheckerErrorFromCode(int errorCode) {
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

    static ArrayList<PirateApp> getApps() {
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
        apps.add(new PirateApp("Lucky Patcher", new String[]{"u", "r", "e", "t", ".", "j", "a",
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
        return apps;
    }

    static boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    static boolean hasPermissions(Context context) {
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

    static String getSystemProperty(String name) throws Exception {
        Class systemPropertyClazz = Class.forName("android.os.SystemProperties");
        return (String) systemPropertyClazz.getMethod("get", new Class[]{String.class}).invoke
                (systemPropertyClazz, name);
    }

}