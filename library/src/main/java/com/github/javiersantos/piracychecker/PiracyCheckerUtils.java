package com.github.javiersantos.piracychecker;

import android.content.Context;

public class PiracyCheckerUtils {

    public static String getAPKSignature(Context context) {
        return UtilsLibrary.getCurrentSignature(context);
    }

}
