package com.github.javiersantos.piracychecker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.github.javiersantos.piracychecker.demo.MainActivity;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.github.javiersantos.piracychecker.enums.PirateApp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * 3. Specific test cases for unauthorized apps. Requires to uninstall an unauthorized app before running this tests.
 */
@RunWith(AndroidJUnit4.class)
public class UnauthorizedAppUninstalledTest {

    @Rule
    public final ActivityTestRule<MainActivity> uiThreadTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void verifyBlockUnauthorizedApps_DONTALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                        .enableUnauthorizedAppsCheck(true)
                        .blockIfUnauthorizedAppUninstalled("piracychecker_preferences", "app_unauthorized")
                        .callback(new PiracyCheckerCallback() {
                            @Override
                            public void allow() {
                                assertTrue("PiracyChecker FAILED: There was an unauthorized app installed previously.", false);
                                signal.countDown();
                            }

                            @Override
                            public void dontAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
                                if (error == PiracyCheckerError.BLOCK_PIRATE_APP)
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

}
