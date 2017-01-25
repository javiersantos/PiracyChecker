package com.github.javiersantos.piracychecker.demo;

import android.os.Bundle;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsActivity;

public class AboutActivity extends LibsActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setIntent(new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTitle(getString(R.string.action_about))
                .withAboutIconShown(true)
                .withAboutDescription(getString(R.string.app_description))
                .withAboutVersionShown(true)
                .withAboutAppName(getString(R.string.app_name))
                .withAutoDetect(true)
                .withLicenseShown(true)
                .intent(this));

        super.onCreate(savedInstanceState);
    }

}