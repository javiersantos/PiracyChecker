@file:Suppress("unused")

package com.github.javiersantos.piracychecker

import android.content.Context
import androidx.fragment.app.Fragment
import com.github.javiersantos.piracychecker.callbacks.AllowCallback
import com.github.javiersantos.piracychecker.callbacks.DoNotAllowCallback
import com.github.javiersantos.piracychecker.callbacks.OnErrorCallback
import com.github.javiersantos.piracychecker.callbacks.PiracyCheckerCallbacksDSL
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError
import com.github.javiersantos.piracychecker.enums.PirateApp

fun Context.piracyChecker(builder: PiracyChecker.() -> Unit): PiracyChecker {
    val checker = PiracyChecker(this)
    checker.builder()
    return checker
}

fun Fragment.piracyChecker(builder: PiracyChecker.() -> Unit): PiracyChecker =
    activity?.piracyChecker(builder) ?: requireContext().piracyChecker(builder)

inline fun PiracyChecker.allow(crossinline allow: () -> Unit = {}) = apply {
    allowCallback(object : AllowCallback {
        override fun allow() = allow()
    })
}

inline fun PiracyChecker.doNotAllow(crossinline doNotAllow: (PiracyCheckerError, PirateApp?) -> Unit = { _, _ -> }) =
    apply {
        doNotAllowCallback(object : DoNotAllowCallback {
            override fun doNotAllow(error: PiracyCheckerError, app: PirateApp?) =
                doNotAllow(error, app)
        })
    }

inline fun PiracyChecker.onError(crossinline onError: (PiracyCheckerError) -> Unit = {}) = apply {
    onErrorCallback(object : OnErrorCallback {
        override fun onError(error: PiracyCheckerError) {
            super.onError(error)
            onError(error)
        }
    })
}

fun PiracyChecker.callback(callbacks: PiracyCheckerCallbacksDSL.() -> Unit) {
    PiracyCheckerCallbacksDSL(this).callbacks()
}