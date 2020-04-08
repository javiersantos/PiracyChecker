package com.github.javiersantos.piracychecker;

import android.content.Context;

import com.github.javiersantos.piracychecker.callbacks.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.demo.MainActivity;
import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.github.javiersantos.piracychecker.enums.PirateApp;
import com.github.javiersantos.piracychecker.utils.LibraryUtilsKt;

import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * 1. Global test cases. Doesn't require any additional step to run.
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class PiracyCheckerTest {

    @Rule
    public final ActivityTestRule<MainActivity> uiThreadTestRule =
        new ActivityTestRule<>(MainActivity.class);

    @Test
    public void verifySigningCertificate_ALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableSigningCertificates("VHZs2aiTBiap/F+AYhYeppy0aF0=")
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            assertTrue("PiracyChecker OK", true);
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            fail(Arrays.toString(LibraryUtilsKt.getApkSignatures(context)));
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
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableSigningCertificates("478yYkKAQF+KST8y4ATKvHkYibo=")
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            fail("PiracyChecker FAILED: The signing certificate is invalid.");
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            if (error == PiracyCheckerError.SIGNATURE_NOT_VALID)
                                assertTrue("PiracyChecker OK", true);
                            else
                                fail("PiracyChecker FAILED : PiracyCheckError is not " +
                                         error.toString());
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
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableInstallerId(InstallerID.GOOGLE_PLAY)
                    .enableInstallerId(InstallerID.AMAZON_APP_STORE)
                    .enableInstallerId(InstallerID.GALAXY_APPS)
                    .enableInstallerId(InstallerID.HUAWEI_APP_GALLERY)
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            fail("PiracyChecker FAILED: The app has been installed using another " +
                                     "store.");
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            if (error == PiracyCheckerError.INVALID_INSTALLER_ID)
                                assertTrue("PiracyChecker OK", true);
                            else
                                fail("PiracyChecker FAILED : PiracyCheckError is not " +
                                         error.toString());
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
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableUnauthorizedAppsCheck()
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            assertTrue("PiracyChecker OK", true);
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            fail(error.toString());
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
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableUnauthorizedAppsCheck()
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            assertTrue("PiracyChecker OK", true);
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            fail(error.toString());
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
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableFoldersCheck()
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            assertTrue("PiracyChecker OK", true);
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            fail(error.toString());
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
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableDebugCheck()
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            fail("PiracyChecker FAILED: Tests are running on a debug build.");
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            if (error == PiracyCheckerError.USING_DEBUG_APP)
                                assertTrue("PiracyChecker OK", true);
                            else
                                fail("PiracyChecker FAILED : PiracyCheckError is not " +
                                         error.toString());
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
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableEmulatorCheck(false)
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            fail("PiracyChecker FAILED: Tests are running on an emulator.");
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            if (error == PiracyCheckerError.USING_APP_IN_EMULATOR)
                                assertTrue("PiracyChecker OK", true);
                            else
                                fail("PiracyChecker FAILED : PiracyCheckError is not " +
                                         error.toString());
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
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableEmulatorCheck(true)
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            fail("PiracyChecker FAILED: Tests are running on an emulator.");
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            if (error == PiracyCheckerError.USING_APP_IN_EMULATOR)
                                assertTrue("PiracyChecker OK", true);
                            else
                                fail("PiracyChecker FAILED : PiracyCheckError is not " +
                                         error.toString());
                            signal.countDown();
                        }
                    })
                    .start();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }
}
