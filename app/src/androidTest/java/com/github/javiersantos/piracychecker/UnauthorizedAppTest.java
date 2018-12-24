package com.github.javiersantos.piracychecker;

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

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertTrue;

/**
 * 2. Specific test cases for unauthorized apps. Requires to install an unauthorized app before
 * running this tests.
 */
@RunWith(AndroidJUnit4.class)
public class UnauthorizedAppTest {
    
    @Rule
    public final ActivityTestRule<MainActivity> uiThreadTestRule =
        new ActivityTestRule<>(MainActivity.class);
    
    @Test
    public void verifyUnauthorizedCustomApp_ALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                    .addAppToCheck(new PirateApp("Demo",
                                                 uiThreadTestRule.getActivity().getPackageName() +
                                                     ".other"))
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            assertTrue("PiracyChecker OK", true);
                            signal.countDown();
                        }
                        
                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @org.jetbrains.annotations.Nullable PirateApp app) {
                            assertTrue("PiracyChecker FAILED : PiracyCheckError is not " +
                                           error.toString(), false);
                            signal.countDown();
                        }
                    })
                    .start();
            }
        });
        
        signal.await(30, TimeUnit.SECONDS);
    }
    
    @Test
    public void verifyUnauthorizedCustomApp_DONTALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                    .addAppToCheck(
                        new PirateApp("Demo", uiThreadTestRule.getActivity().getPackageName()))
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            assertTrue(
                                "PiracyChecker FAILED: There is a custom unauthorized app " +
                                    "installed.",
                                false);
                            signal.countDown();
                        }
                        
                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @org.jetbrains.annotations.Nullable PirateApp app) {
                            if (error == PiracyCheckerError.PIRATE_APP_INSTALLED)
                                assertTrue("PiracyChecker OK", true);
                            else
                                assertTrue("PiracyChecker FAILED : PiracyCheckError is not " +
                                               error.toString(), false);
                            signal.countDown();
                        }
                    })
                    .start();
            }
        });
        
        signal.await(30, TimeUnit.SECONDS);
    }
    
    @Test
    public void verifyUnauthorizedApps_DONTALLOW() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        uiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PiracyChecker(InstrumentationRegistry.getTargetContext())
                    .enableUnauthorizedAppsCheck()
                    .blockIfUnauthorizedAppUninstalled("piracychecker_preferences",
                                                       "app_unauthorized")
                    .callback(new PiracyCheckerCallback() {
                        @Override
                        public void allow() {
                            assertTrue(
                                "PiracyChecker FAILED: There is an unauthorized app installed.",
                                false);
                            signal.countDown();
                        }
                        
                        @Override
                        public void doNotAllow(@NotNull PiracyCheckerError error,
                                               @org.jetbrains.annotations.Nullable PirateApp app) {
                            if (error == PiracyCheckerError.PIRATE_APP_INSTALLED)
                                assertTrue("PiracyChecker OK", true);
                            else
                                assertTrue("PiracyChecker FAILED : PiracyCheckError is not " +
                                               error.toString(), false);
                            signal.countDown();
                        }
                    })
                    .start();
            }
        });
        
        signal.await(30, TimeUnit.SECONDS);
    }
    
}
