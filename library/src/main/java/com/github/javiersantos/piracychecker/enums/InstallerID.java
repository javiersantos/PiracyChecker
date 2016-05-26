package com.github.javiersantos.piracychecker.enums;

public enum InstallerID {
    GOOGLE_PLAY("com.android.vending");

    private final String text;

    private InstallerID(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

}
