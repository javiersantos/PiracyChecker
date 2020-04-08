package com.github.javiersantos.piracychecker;

import android.content.Context;

import com.github.javiersantos.piracychecker.callbacks.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.demo.MainActivity;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.github.javiersantos.piracychecker.enums.PirateApp;

import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * 3. Specific test cases for unauthorized apps. Requires to uninstall an unauthorized app before
 * running this tests.
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class UnauthorizedAppUninstalledTest {

    @Rule
    public final ActivityTestRule<MainActivity> uiThreadTestRule =
        new ActivityTestRule<>(MainActivity.class);

    @Test
    public void verifyBlockUnauthorizedApps_DONTALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Context context =
                    InstrumentationRegistry.getInstrumentation().getTargetContext();
                new PiracyChecker(context)
                    .enableUnauthorizedAppsCheck()
                    .blockIfUnauthorizedAppUninstalled("piracychecker_preferences",
                                                       "app_unauthorized")
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            fail("PiracyChecker FAILED: There was an unauthorized app installed " +
                                     "previously.");
                            signal.countDown();
                        }

                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @Nullable PirateApp app) {
                            if (error == PiracyCheckerError.BLOCK_PIRATE_APP)
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
}