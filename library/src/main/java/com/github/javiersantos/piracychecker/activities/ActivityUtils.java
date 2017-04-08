package com.github.javiersantos.piracychecker.activities;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;

class ActivityUtils {

    static String getAppName(Context context) {
        return context.getString(context.getApplicationInfo().labelRes);
    }

    static void setupLightStatusBar(@NonNull View view, boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            if (enable)
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            else
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

}