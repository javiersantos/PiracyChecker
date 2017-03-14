package com.github.javiersantos.piracychecker.demo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.github.javiersantos.piracychecker.PiracyChecker;
import com.github.javiersantos.piracychecker.PiracyCheckerUtils;
import com.github.javiersantos.piracychecker.enums.Display;
import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

public class MainActivity extends AppCompatActivity {
    private Display piracyCheckerDisplay = Display.DIALOG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        RadioGroup radioDisplay = (RadioGroup) findViewById(R.id.radio_display);

        setSupportActionBar(toolbar);

        fab.setImageDrawable(new IconicsDrawable(this).icon(MaterialDesignIconic.Icon.gmi_github)
                .color(Color.WHITE).sizeDp(24));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/javiersantos/PiracyChecker")));
            }
        });

        radioDisplay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.radio_dialog:
                        piracyCheckerDisplay = Display.DIALOG;
                        break;
                    case R.id.radio_activity:
                        piracyCheckerDisplay = Display.ACTIVITY;
                        break;
                }
            }
        });
    }

    public void verifySignature(View view) {
        new PiracyChecker(this)
                .display(piracyCheckerDisplay)
                .enableSigningCertificate("478yYkKAQF+KST8y4ATKvHkYibo=")
                .start();
    }

    public void readSignature(View view) {
        Log.e("Signature", PiracyCheckerUtils.getAPKSignature(this));
        new AlertDialog.Builder(this)
                .setTitle("APK")
                .setMessage(PiracyCheckerUtils.getAPKSignature(this))
                .show();
    }

    public void verifyInstallerId(View view) {
        new PiracyChecker(this)
                .display(piracyCheckerDisplay)
                .enableInstallerId(InstallerID.GOOGLE_PLAY)
                .start();
    }

    public void verifyUnauthorizedApps(View view) {
        new PiracyChecker(this)
                .display(piracyCheckerDisplay)
                .enableUnauthorizedAppsCheck()
                //.blockIfUnauthorizedAppUninstalled("license_checker", "block")
                .start();
    }

    public void verifyStores(View view) {
        new PiracyChecker(this)
                .display(piracyCheckerDisplay)
                .enableStoresCheck()
                .start();
    }

    public void verifyDebug(View view) {
        new PiracyChecker(this)
                .display(piracyCheckerDisplay)
                .enableDebugCheck()
                .start();
    }

    public void verifyEmulator(View view) {
        new PiracyChecker(this)
                .display(piracyCheckerDisplay)
                .enableEmulatorCheck()
                .start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_about).setIcon(new IconicsDrawable(this).icon
                (MaterialDesignIconic.Icon.gmi_info).color(Color.WHITE).actionBar());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
