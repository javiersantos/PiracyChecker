package com.github.javiersantos.piracychecker.utils

import android.content.Context
import android.preference.PreferenceManager
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
                    if (i > 0) {
                        sb.append(" ")
                    }
                    sb.append(java.lang.Byte.toString(it[i]))
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
        val saltStr = saltString
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(KEY_SALT, saltStr)
            .apply()
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
                    if (prefs.contains(
                            KEY_SALT)) {
                        bytesFromString(
                            prefs.getString(
                                KEY_SALT,
                                null))
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
            if (mSalt == null) {
                generateSalt(context)
            }
        }
        return mSalt
    }
}