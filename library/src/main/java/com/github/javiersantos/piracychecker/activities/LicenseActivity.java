package com.github.javiersantos.piracychecker.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.javiersantos.piracychecker.R;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class LicenseActivity extends AppCompatActivity {
    private String description;
    @ColorRes
    private int colorPrimary;
    @ColorRes
    private int colorPrimaryDark;
    private boolean withLightStatusBar;
    @LayoutRes
    private int layoutXML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        getIntentData();
        setActivityStyle();
        setActivityData();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            description = getIntent().getStringExtra("content");
            colorPrimary = getIntent().getIntExtra("colorPrimary",
                                                   ContextCompat
                                                           .getColor(this, R.color.colorPrimary));
            colorPrimaryDark = getIntent().getIntExtra("colorPrimaryDark",
                                                       ContextCompat.getColor(this,
                                                                              R.color.colorPrimaryDark));
            withLightStatusBar = getIntent().getBooleanExtra("withLightStatusBar", false);
            layoutXML = getIntent().getIntExtra("layoutXML", -1);
        }
    }

    private void setActivityStyle() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, colorPrimary));
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(ActivityUtils.getAppName(this));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, colorPrimaryDark));
        }

        ActivityUtils.setupLightStatusBar(getWindow().getDecorView(), withLightStatusBar);
    }

    private void setActivityData() {
        FrameLayout frameLayout = findViewById(R.id.mainContainer);

        LayoutInflater factory = LayoutInflater.from(this);
        View inflateView;
        if (layoutXML == -1) {
            inflateView = factory.inflate(R.layout.activity_license_default, null);
            TextView activityDescription =
                    inflateView.findViewById(R.id.piracy_checker_description);
            activityDescription.setText(description);
        } else
            inflateView = factory.inflate(layoutXML, null);

        frameLayout.addView(inflateView);
    }

}