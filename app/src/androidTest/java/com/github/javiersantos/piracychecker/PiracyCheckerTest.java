package com.github.javiersantos.piracychecker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.github.javiersantos.piracychecker.enums.PirateApp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PiracyCheckerTest {

    @Rule
    public UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();

    @Test
    public void verifySigningCertificate_OK() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                        .enableSigningCertificate("VHZs2aiTBiap/F+AYhYeppy0aF0=")
                        .callback(new PiracyCheckerCallback() {
                            @Override
                            public void allow() {
                                assertTrue("PiracyChecker OK", true);
                                signal.countDown();
                            }

                            @Override
                            public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                                assertTrue("PiracyChecker FAILED: The signing certificate is valid.", false);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifySigningCertificate_FAILED() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                        .enableSigningCertificate("478yYkKAQF+KST8y4ATKvHkYibo=")
                        .callback(new PiracyCheckerCallback() {
                            @Override
                            public void allow() {
                                assertTrue("PiracyChecker FAILED: The signing certificate is invalid.", false);
                                signal.countDown();
                            }

                            @Override
                            public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                                assertTrue("PiracyChecker OK", true);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

}
