package com.github.javiersantos.piracychecker.enums

import android.text.TextUtils

class PirateApp(name: String, pack: Array<String>, type: AppType = AppType.OTHER) {
    var name: String? = null
        private set
    var type: AppType? = null
        private set
    private var pack: Array<String>? = null
    
    init {
        this.name = name
        this.pack = pack.clone()
        this.type = type
    }
    
    @Deprecated("Deprecated in favor of packageName", ReplaceWith("packageName"))
    val `package`: String
        get() = packageName
    
    val packageName: String
        get() {
            val sb = StringBuilder()
            pack?.forEach { sb.append(it) }
            return sb.toString()
        }
    
    @JvmOverloads
    constructor(name: String, appPackage: String, type: AppType = AppType.OTHER) :
        this(name, TextUtils.split(appPackage, ""), type)
}