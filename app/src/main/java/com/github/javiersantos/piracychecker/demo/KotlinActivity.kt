package com.github.javiersantos.piracychecker.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.widget.RadioGroup
import com.github.javiersantos.piracychecker.allow
import com.github.javiersantos.piracychecker.callback
import com.github.javiersantos.piracychecker.doNotAllow
import com.github.javiersantos.piracychecker.enums.Display
import com.github.javiersantos.piracychecker.enums.InstallerID
import com.github.javiersantos.piracychecker.onError
import com.github.javiersantos.piracychecker.piracyChecker
import com.github.javiersantos.piracychecker.utils.apkSignature

@Suppress("unused")
class KotlinActivity : AppCompatActivity() {
    private var piracyCheckerDisplay = Display.DIALOG
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val radioDisplay = findViewById<RadioGroup>(R.id.radio_display)
        
        setSupportActionBar(toolbar)
        
        radioDisplay.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.radio_dialog -> piracyCheckerDisplay = Display.DIALOG
                R.id.radio_activity -> piracyCheckerDisplay = Display.ACTIVITY
            }
        }
        
        // Show APK signature
        Log.e("Signature", apkSignature)
    }
    
    fun toGithub() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/javiersantos/piracyChecker")))
    }
    
    fun verifySignature() {
        piracyChecker {
            display(piracyCheckerDisplay)
            enableSigningCertificate("478yYkKAQF+KST8y4ATKvHkYibo=") // Wrong signature
            //enableSigningCertificate("VHZs2aiTBiap/F+AYhYeppy0aF0=") // Right signature
        }.start()
    }
    
    fun readSignature() {
        Log.e("Signature", apkSignature)
        AlertDialog.Builder(this)
            .setTitle("APK")
            .setMessage(apkSignature)
            .show()
    }
    
    fun verifyInstallerId() {
        piracyChecker {
            display(piracyCheckerDisplay)
            enableInstallerId(InstallerID.GOOGLE_PLAY)
        }.start()
    }
    
    fun verifyUnauthorizedApps() {
        piracyChecker {
            display(piracyCheckerDisplay)
            enableUnauthorizedAppsCheck()
            //blockIfUnauthorizedAppUninstalled("license_checker", "block")
        }.start()
    }
    
    fun verifyStores() {
        piracyChecker {
            display(piracyCheckerDisplay)
            enableStoresCheck()
        }.start()
    }
    
    fun verifyDebug() {
        piracyChecker {
            display(piracyCheckerDisplay)
            enableDebugCheck()
            callback {
                allow {
                    // Do something when the user is allowed to use the app
                }
                doNotAllow { piracyCheckerError, pirateApp ->
                    // You can either do something specific when the user is not allowed to use the app
                    // Or manage the error, using the 'error' parameter, yourself (Check errors at {@link PiracyCheckerError}).
                    
                    // Additionally, if you enabled the check of pirate apps and/or third-party stores, the 'app' param
                    // is the app that has been detected on device. App can be null, and when null, it means no pirate app or store was found,
                    // or you disabled the check for those apps.
                    // This allows you to let users know the possible reasons why license is been invalid.
                }
                onError { error ->
                    // This method is not required to be implemented/overriden but...
                    // You can either do something specific when an error occurs while checking the license,
                    // Or manage the error, using the 'error' parameter, yourself (Check errors at {@link PiracyCheckerError}).
                }
            }
        }.start()
    }
    
    fun verifyEmulator() {
        piracyChecker {
            display(piracyCheckerDisplay)
            enableEmulatorCheck(false)
        }.start()
    }
}