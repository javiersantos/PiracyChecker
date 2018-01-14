package com.github.javiersantos.piracychecker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.javiersantos.piracychecker.demo.MainActivity;
import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.github.javiersantos.piracychecker.enums.PirateApp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * 1. Global test cases. Doesn't require any additional step to run.
 */
@RunWith(AndroidJUnit4.class)
public class PiracyCheckerTest {

    @Rule
    public final ActivityTestRule<MainActivity> uiThreadTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void verifySigningCertificate_ALLOW() throws Throwable {
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
                                assertTrue(error.toString() + " - Current signature: " + LibraryUtils.getCurrentSignature(InstrumentationRegistry.getTargetContext()), false);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifySigningCertificate_DONTALLOW() throws Throwable {
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
                                if (error == PiracyCheckerError.SIGNATURE_NOT_VALID)
                                    assertTrue("PiracyChecker OK", true);
                                else
                                    assertTrue("PiracyChecker FAILED : PiracyCheckError is not " + error.toString(), false);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifyInstaller_DONTALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                        .enableInstallerId(InstallerID.GOOGLE_PLAY)
                        .enableInstallerId(InstallerID.AMAZON_APP_STORE)
                        .enableInstallerId(InstallerID.GALAXY_APPS)
                        .callback(new PiracyCheckerCallback() {
                            @Override
                            public void allow() {
                                assertTrue("PiracyChecker FAILED: The app has been installed using another store.", false);
                                signal.countDown();
                            }

                            @Override
                            public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                                if (error == PiracyCheckerError.INVALID_INSTALLER_ID)
                                    assertTrue("PiracyChecker OK", true);
                                else
                                    assertTrue("PiracyChecker FAILED : PiracyCheckError is not " + error.toString(), false);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifyUnauthorizedApps_ALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                        .enableUnauthorizedAppsCheck()
                        .callback(new PiracyCheckerCallback() {
                            @Override
                            public void allow() {
                                assertTrue("PiracyChecker OK", true);
                                signal.countDown();
                            }

                            @Override
                            public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                                assertTrue(error.toString(), false);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifyThirdPartyStores_ALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                        .enableUnauthorizedAppsCheck()
                        .callback(new PiracyCheckerCallback() {
                            @Override
                            public void allow() {
                                assertTrue("PiracyChecker OK", true);
                                signal.countDown();
                            }

                            @Override
                            public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                                assertTrue(error.toString(), false);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifyDeepPirate_ALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                        .enableFoldersCheck(true)
                        .callback(new PiracyCheckerCallback() {
                            @Override
                            public void allow() {
                                assertTrue("PiracyChecker OK", true);
                                signal.countDown();
                            }

                            @Override
                            public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                                assertTrue(error.toString(), false);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifyDebug_DONTALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                        .enableDebugCheck()
                        .callback(new PiracyCheckerCallback() {
                            @Override
                            public void allow() {
                                assertTrue("PiracyChecker FAILED: Tests are running on a debug build.", false);
                                signal.countDown();
                            }

                            @Override
                            public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                                if (error == PiracyCheckerError.USING_DEBUG_APP)
                                    assertTrue("PiracyChecker OK", true);
                                else
                                    assertTrue("PiracyChecker FAILED : PiracyCheckError is not " + error.toString(), false);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifyEmulator_DONTALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                        .enableEmulatorCheck(false)
                        .callback(new PiracyCheckerCallback() {
                            @Override
                            public void allow() {
                                assertTrue("PiracyChecker FAILED: Tests are running on an emulator.", false);
                                signal.countDown();
                            }

                            @Override
                            public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                                if (error == PiracyCheckerError.USING_APP_IN_EMULATOR)
                                    assertTrue("PiracyChecker OK", true);
                                else
                                    assertTrue("PiracyChecker FAILED : PiracyCheckError is not " + error.toString(), false);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void verifyEmulatorDeep_DONTALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                        .enableEmulatorCheck(true)
                        .callback(new PiracyCheckerCallback() {
                            @Override
                            public void allow() {
                                assertTrue("PiracyChecker FAILED: Tests are running on an emulator.", false);
                                signal.countDown();
                            }

                            @Override
                            public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                                if (error == PiracyCheckerError.USING_APP_IN_EMULATOR)
                                    assertTrue("PiracyChecker OK", true);
                                else
                                    assertTrue("PiracyChecker FAILED : PiracyCheckError is not " + error.toString(), false);
                                signal.countDown();
                            }
                        })
                        .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }

}
