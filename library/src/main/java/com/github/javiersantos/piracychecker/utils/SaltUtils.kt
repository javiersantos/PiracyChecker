package com.github.javiersantos.piracychecker.utils

import android.content.Context
import androidx.preference.PreferenceManager
import java.util.Random

/**
 * Credits to Aidan Follestad (afollestad)
 */
internal object SaltUtils {
    private const val KEY_SALT = "salty-salt"
    private var mSalt: ByteArray? = null
    
    private val saltString: String
        get() {
            val sb = StringBuilder()
            mSalt?.let {
                for (i in it.indices) {
                    if (i > 0) sb.append(" ")
                    sb.append(it[i].toString())
                }
            }
            return sb.toString()
        }
    
    private fun generateSalt(context: Context?) {
        mSalt = ByteArray(20)
        val randomGenerator = Random()
        mSalt?.let {
            for (i in 0..19) {
                it[i] = (randomGenerator.nextInt(600) - 300).toByte()
            }
        }
        context ?: return
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(KEY_SALT, saltString).apply()
    }
    
    private fun bytesFromString(string: String): ByteArray {
        val split = string.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val data = ByteArray(split.size)
        for (i in split.indices) {
            data[i] = java.lang.Byte.parseByte(split[i])
        }
        return data
    }
    
    fun getSalt(context: Context?): ByteArray? {
        if (mSalt == null) {
            mSalt = context?.let {
                try {
                    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                    if (prefs.contains(KEY_SALT)) {
                        val saltFromPrefs = prefs.getString(KEY_SALT, null)
                        saltFromPrefs?.let { bytesFromString(it) }
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
            if (mSalt == null) generateSalt(context)
        }
        return mSalt
    }
}