package com.github.javiersantos.piracychecker.activities;

import android.content.Context;

class ActivityUtils {

    static String getAppName(Context context) {
        return context.getString(context.getApplicationInfo().labelRes);
    }

}