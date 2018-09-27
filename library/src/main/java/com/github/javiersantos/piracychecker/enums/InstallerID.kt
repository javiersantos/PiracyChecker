package com.github.javiersantos.piracychecker.enums

import java.util.ArrayList
import java.util.Arrays

enum class InstallerID(private val text: String) {
    GOOGLE_PLAY("com.android.vending|com.google.android.feedback"),
    AMAZON_APP_STORE("com.amazon.venezia"),
    GALAXY_APPS("com.sec.android.app.samsungapps");
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    override fun toString(): String {
        return text
    }
    
    fun toIDs(): List<String> = if (text.contains("|")) {
        val split = text.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        ArrayList(Arrays.asList(*split))
    } else {
        ArrayList(listOf(text))
    }
}
