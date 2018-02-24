package com.github.javiersantos.piracychecker.enums;

import android.support.annotation.NonNull;
import android.text.TextUtils;

public class PirateApp {
    private String name;
    private String[] pack;

    public PirateApp(@NonNull String name, @NonNull String[] pack) {
        this.name = name;
        this.pack = pack;
    }

    public PirateApp(@NonNull String name, @NonNull String appPackage) {
        this.name = name;
        this.pack = TextUtils.split(appPackage, "");
    }

    public String getName() {
        return name;
    }

    @Deprecated
    public String[] getPack() {
        return pack;
    }

    public String getPackage() {
        StringBuilder sb = new StringBuilder();
        for (String s : pack) {
            sb.append(s);
        }
        return sb.toString();
    }

    public boolean isUnauthorized() {
        return (name.equalsIgnoreCase("Lucky Patcher") || name.equalsIgnoreCase("Freedom") ||
                name.equalsIgnoreCase("Uret Patcher") || name.equalsIgnoreCase("CreeHack"));
    }
}