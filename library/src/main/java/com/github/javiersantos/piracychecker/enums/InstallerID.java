package com.github.javiersantos.piracychecker.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum InstallerID {
    GOOGLE_PLAY("com.android.vending|com.google.android.feedback"),
    AMAZON_APP_STORE("com.amazon.venezia"),
    GALAXY_APPS("com.sec.android.app.samsungapps");

    private final String text;

    InstallerID(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

    public List<String> toIDs() {
        if (text.contains("|")) {
            String[] split = text.split("\\|");
            return new ArrayList<>(Arrays.asList(split));
        } else {
            return new ArrayList<>(Collections.singletonList(text));
        }
    }

}
