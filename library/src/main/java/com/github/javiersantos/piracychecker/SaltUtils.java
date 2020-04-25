package com.github.javiersantos.piracychecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Random;

/**
 * Credits to Aidan Follestad (afollestad)
 */
class SaltUtils {
    private static final String KEY_SALT = "piracy-salt";
    private static byte[] mSalt;

    private static void generateSalt(Context context) {
        mSalt = new byte[20];
        final Random randomGenerator = new Random();
        for (int i = 0; i < 20; ++i) {
            mSalt[i] = (byte) (randomGenerator.nextInt(600) - 300);
        }
        final String saltStr = getSaltString();
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_SALT, saltStr).apply();
    }

    private static String getSaltString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mSalt.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(mSalt[i]);
        }
        return sb.toString();
    }

    private static byte[] bytesFromString(String string) {
        final String[] split = string.split(" ");
        final byte[] data = new byte[split.length];
        for (int i = 0; i < split.length; i++) {
            data[i] = Byte.parseByte(split[i]);
        }
        return data;
    }

    @SuppressWarnings("ConstantConditions")
    static byte[] getSalt(Context context) {
        if (mSalt == null) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (prefs.contains(KEY_SALT)) {
                mSalt = bytesFromString(prefs.getString(KEY_SALT, null));
            }
            if (mSalt == null) {
                generateSalt(context);
            }
        }
        return mSalt;
    }
}