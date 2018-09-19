package com.github.javiersantos.piracychecker.enums;

import android.support.annotation.NonNull;
import android.text.TextUtils;

public class PirateApp {
    private String name;
    private String[] pack;
    private AppType type;

    public PirateApp(@NonNull String name, @NonNull String[] pack, @NonNull AppType type) {
        this.name = name;
        this.pack = pack.clone();
        this.type = type;
    }

    public PirateApp(@NonNull String name, @NonNull String appPackage, @NonNull AppType type) {
        this.name = name;
        this.pack = TextUtils.split(appPackage, "");
        this.type = type;
    }

    public PirateApp(@NonNull String name, @NonNull String appPackage) {
        this(name, appPackage, AppType.OTHER);
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

    public AppType getType() {
        return type;
    }
}
