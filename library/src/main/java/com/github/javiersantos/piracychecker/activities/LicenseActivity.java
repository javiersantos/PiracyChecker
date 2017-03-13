package com.github.javiersantos.piracychecker.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.javiersantos.piracychecker.R;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        getIntentData();
    }

    private void getIntentData() {
        TextView activityDescription = (TextView) findViewById(R.id.piracy_checker_description);
        if (getIntent() != null) {
            activityDescription.setText(getIntent().getStringExtra("piracy_checker"));
        }
    }

}
