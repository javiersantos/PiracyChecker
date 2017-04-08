package com.github.javiersantos.piracychecker.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.javiersantos.piracychecker.R;

public class LicenseActivity extends AppCompatActivity {
    private String description;
    private int colorPrimary;
    private int colorPrimaryDark;
    private boolean withLightStatusBar;

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
                    ContextCompat.getColor(this, R.color.colorPrimary));
            colorPrimaryDark = getIntent().getIntExtra("colorPrimaryDark",
                    ContextCompat.getColor(this, R.color.colorPrimaryDark));
            withLightStatusBar = getIntent().getBooleanExtra("withLightStatusBar", false);
        }
    }

    private void setActivityStyle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            toolbar.setTitle(ActivityUtils.getAppName(this));
            toolbar.setBackgroundColor(colorPrimary);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(colorPrimaryDark);
        }

        ActivityUtils.setupLightStatusBar(getWindow().getDecorView(), withLightStatusBar);
    }

    private void setActivityData() {
        TextView activityDescription = (TextView) findViewById(R.id.piracy_checker_description);
        activityDescription.setText(description);
    }

}