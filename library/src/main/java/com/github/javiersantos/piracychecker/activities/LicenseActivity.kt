package com.github.javiersantos.piracychecker.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

import com.github.javiersantos.piracychecker.R

class LicenseActivity : AppCompatActivity() {
    private var description: String? = null
    @ColorRes
    private var colorPrimary: Int = 0
    @ColorRes
    private var colorPrimaryDark: Int = 0
    private var withLightStatusBar: Boolean = false
    @LayoutRes
    private var layoutXML: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
        getIntentData()
        setActivityStyle()
        setActivityData()
    }
    
    private fun getIntentData() {
        if (intent != null) {
            description = intent.getStringExtra("content")
            colorPrimary = intent.getIntExtra(
                "colorPrimary", ContextCompat.getColor(this, R.color.colorPrimary))
            colorPrimaryDark = intent.getIntExtra(
                "colorPrimaryDark", ContextCompat.getColor(this, R.color.colorPrimaryDark))
            withLightStatusBar = intent.getBooleanExtra("withLightStatusBar", false)
            layoutXML = intent.getIntExtra("layoutXML", -1)
        }
    }
    
    private fun setActivityStyle() {
        val toolbar = findViewById<View>(R.id.toolbar) as? Toolbar
        toolbar?.setBackgroundColor(ContextCompat.getColor(this, colorPrimary))
        setSupportActionBar(toolbar)
        supportActionBar?.title = getAppName()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, colorPrimaryDark)
        }
        
        window.decorView.setupLightStatusBar(withLightStatusBar)
    }
    
    @SuppressLint("InflateParams")
    private fun setActivityData() {
        val frameLayout = findViewById<View>(R.id.mainContainer) as FrameLayout
        
        val factory = LayoutInflater.from(this)
        val inflateView: View
        if (layoutXML == -1) {
            inflateView = factory.inflate(R.layout.activity_license_default, null)
            val activityDescription =
                inflateView.findViewById<View>(R.id.piracy_checker_description) as TextView
            activityDescription.text = description
        } else
            inflateView = factory.inflate(layoutXML, null)
        
        frameLayout.addView(inflateView)
    }
}