<h1 align="center">PiracyChecker <a href="https://github.com/javiersantos/PiracyChecker#how-to-include"><img src="https://jitpack.io/v/javiersantos/PiracyChecker.svg"></a></h1>
<h4 align="center">Android Library</h4>

<p align="center">
  <a target="_blank" href="https://android-arsenal.com/api?level=14"><img src="https://img.shields.io/badge/API-14%2B-orange.svg"></a>
  <a target="_blank" href="https://travis-ci.org/javiersantos/PiracyChecker"><img src="https://travis-ci.org/javiersantos/PiracyChecker.svg?branch=master"></a>
  <a target="_blank" href="http://android-arsenal.com/details/1/3641"><img src="https://img.shields.io/badge/Android%20Arsenal-PiracyChecker-blue.svg"></a>
</p>

<p align="center">An Android library that prevents your app from being pirated / cracked using Google Play Licensing (LVL), APK signature protection and more.</p>

<i><p align="center">
  Owner & Author: <a target="_blank" href="https://github.com/javiersantos">Javier Santos</a><br>
  Co-Author: <a target="_blank" href="https://jahirfiquitiva.com/">Jahir Fiquitiva</a>
</p></i>

## Disclaimer
This library applies some techniques to help protect your app's users and attempt to thwart reverse engineers and attackers. BUT, this isn't guaranteed to stop your app from getting pirated. There is no such thing as 100% security, and a determined and skilled attacker with enough time, could remove these checks from the code. The real objective here is to raise the bar out of reach of opportunist and automatic attackers.

Some of the techniques included in this library can be found [here](https://www.airpair.com/android/posts/adding-tampering-detection-to-your-android-app).


## How to include
Add the repository to your project **build.gradle**:

```gradle
allprojects {
    repositories {
        maven {
            url "https://jitpack.io"
        }
    }
}
```

And add the library to your module **build.gradle**:

**AndroidX**

```gradle
dependencies {
    implementation 'com.github.javiersantos:PiracyChecker:1.2.8'
}
```

**Pre AndroidX (no longer supported)**

```gradle
dependencies {
    implementation 'com.github.javiersantos:PiracyChecker:1.2.4'
}
```


## Recommendations
* Always enable ProGuard in your production release. Always, without exceptions.
* PiracyChecker should be included in your `onCreate` method in order to check for a valid license as soon as possible.
* It's recommended to show a new Activity instead of a Dialog when the license is not valid. This way you make sure that the main activity of the app is finished. See "[Display results in a Dialog or a new Activity](https://github.com/javiersantos/PiracyChecker#display-results-in-a-dialog-or-a-new-activity)".  
* Don't forget to enable ProGuard ;)

## Usage

### Verify Google Play Licensing (LVL)
Google Play offers a licensing service that lets you enforce licensing policies for applications that you publish on Google Play. With Google Play Licensing, your application can query Google Play to obtain the licensing status for the current user.

Any application that you publish through Google Play can use the Google Play Licensing service. No special account or registration is needed.

For more information check out the [Google Developers page](https://developer.android.com/google/play/licensing/index.html).

```kotlin
piracyChecker {
	enableGooglePlayLicensing("BASE_64_LICENSE_KEY")
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableGooglePlayLicensing("BASE_64_LICENSE_KEY")
	...
	.start();
```

</details><br>



In order to retrieve your BASE64 license key your app must be uploaded to the [Google Play Developer Console](https://play.google.com/apps/publish/). Then access to your app -> Services and APIs.

When using Google Play Licensing your should call `.destroy()` in the `onDestroy()` method of your Activity to avoid multiple instances of the service running. Have a look to the Wiki for a [sample Activity](https://github.com/javiersantos/PiracyChecker/wiki/Using-Google-Play-Licensing-(LVL)) with `destroy()`.

### Verify your app's signing certificates (signatures)
In a nutshell, developers must sign applications with their private key/certificate (contained in a .keystore file) before the app can be installed on user devices. The signing certificate must stay consistent throughout the life of the app, and typically have an expiry date of 25 years in the future.

The app signatures will be broken if the .apk is altered in any way — unsigned apps cannot typically be installed. We can imagine an attacker removing license-checking code to enable full app features without paying, for instance. A more dangerous example would be altering the .apk to include malware in a legitimate app to harvest sensitive user data. In order for the altered .apk to be installed, the attacker must resign it.


```kotlin
piracyChecker {
	enableSigningCertificates("478yYkKAQF+KST8y4ATKvHkYibo=") // The original APK signature for the PRODUCTION version
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableSigningCertificates("478yYkKAQF+KST8y4ATKvHkYibo=") // The original APK signature for the PRODUCTION version
	...
	.start();
```

</details><br>



**Don't use this method when using [Google Play App Signing](https://support.google.com/googleplay/android-developer/answer/7384423?hl=en) since Google removes the original signature and add another one, so this method will fail.**

**BE CAREFUL!!** Your app signature can be retrieved using a PiracyCheckerUtils method. Make sure that you have signed your APK using your PRODUCTION keystore (not using the DEBUG one) and installed the version that you plan to distribute. Then copy the signature returned by this method on the console and paste in `enableSigningCertificate("YOUR_APK_SIGNATURE")`

```kotlin
// This method will print your app signatures in the console
apkSignatures.forEach { Log.e("SIGNATURE", it) }
```

<details><summary><b>Java Sample</b></summary>

```java
// This method will print your app signatures in the console
for (String signature : LibraryUtilsKt.getApkSignatures(this)) {
    Log.e("SIGNATURE", signature);
}
```

</details><br>



### Verify the installer
If you only plan to distribute the app on a particular store this technique will block from installing the app using any another store.

Supported stores: Google Play, Amazon App Store and Samsung Galaxy Apps.

```kotlin
piracyChecker {
	enableInstallerId(InstallerID.GOOGLE_PLAY, InstallerID.AMAZON_APP_STORE, InstallerID.GALAXY_APPS)
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableInstallerId(InstallerID.GOOGLE_PLAY, InstallerID.AMAZON_APP_STORE, InstallerID.GALAXY_APPS)
	...
	.start();
```

</details><br>



**BE CAREFUL!!** This is a really restrictive technique since it will block your app from being installed using another market or directly installing the .apk on the device. It isn't recommended for most cases.

### Verify the use of pirate apps
If you want to check if user has pirate apps installed, you can use this code.

It will check for: Lucky Patcher, Uret Patcher, Freedom, CreeHack and HappyMod.

```kotlin
piracyChecker {
	enableUnauthorizedAppsCheck()
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableUnauthorizedAppsCheck()
	...
	.start();
```

</details><br>



#### Add custom apps to check
Since version 1.2.2 you can add additional apps to be checked using this code:

```kotlin
val app = PirateApp("Lucky Patcher", "the.package.name")
piracyChecker {
	addAppToCheck(app)
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
PirateApp app = new PirateApp("Lucky Patcher", "the.package.name");
new PiracyChecker(this)
	.addAppToCheck(app)
	...
	.start();
```

</details><br>



You can block the app even when this pirate apps has been uninstalled. This prevents the app from being patched and then uninstall the pirate app in order to continue using your app. The library will save a `SharedPreference` value to know when a pirate app has been detected.

There are two ways to do this:

Define the `SharedPreferences` and the name of the preference where you want to save the result.

```kotlin
piracyChecker {
	enableUnauthorizedAppsCheck()
	blockIfUnauthorizedAppUninstalled(preferences, "app_unauthorized") // Change "app_unauthorized" with your own value
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableUnauthorizedAppsCheck()
	.blockIfUnauthorizedAppUninstalled(preferences, "app_unauthorized") // Change "app_unauthorized" with your own value
	...
	.start();
```

</details><br>



Define the `SharedPreferences` name and the name of the preference where you want to save the result.

```kotlin
piracyChecker {
	enableUnauthorizedAppsCheck()
	blockIfUnauthorizedAppUninstalled("license_preferences", "app_unauthorized") // Change "license_preferences" and "app_unauthorized" with your own value
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableUnauthorizedAppsCheck()
	.blockIfUnauthorizedAppUninstalled("license_preferences", "app_unauthorized") // Change "license_preferences" and "app_unauthorized" with your own value
	...
	.start();
```

</details><br>



### Verify the use of third-party store apps
If you want to check if user has third-party store apps installed, you can use this code.

It will check for: Aptoide, BlackMart, Mobogenie, 1Mobile, GetApk, GetJar, SlideMe and ACMarket.

```kotlin
piracyChecker {
	enableStoresCheck()
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableStoresCheck()
	...
	.start();
```

</details><br>



### Enable deep pirate and third-party store apps check
If you want to check if these kind of apps left some files that could make your app work as pirated still, you can enable its check as follows:

```kotlin
piracyChecker {
	enableFoldersCheck()
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableFoldersCheck()
	...
	.start();
```

</details><br>



If you also want to check for `.apk` files in certain system folders, you can enable it like so:

```kotlin
piracyChecker {
	enableAPKCheck()
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableAPKCheck()
	...
	.start();
```

</details><br>



**BE CAREFUL!** This means, that some times, the app will be recognized as pirated even after those pirate and third-party store apps were uninstalled. Set it to false if you don't like this behaviour/approach.

### Verify if app is a debug build
Allowing apps to be debugged when installed on an Android device is something that, as developers, we only enable during the development process. Therefore, if debugging occurs on a live build of your app, it's likely that someone other than you is trying to analyze the app.


```kotlin
piracyChecker {
	enableDebugCheck()
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableDebugCheck()
	...
	.start();
```

</details><br>



### Verify if app is being run in an emulator
If your app is running on an emulator outside the development process, it gives an indication that someone other than you is trying to analyze the app.

**Warning!**: Using deep check can cause crashes in some specific devices.

```kotlin
val deepCheck = false
piracyChecker {
	.enableEmulatorCheck(deepCheck)
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
boolean deep = false;
new PiracyChecker(this)
	.enableEmulatorCheck(deep)
	...
	.start();
```

</details><br>



**Note:** the deep boolean with make the library do extra checks to detect if device is an emulator or not. It could lead to some weird crashes, so be wise when using it.

### Save the result of the license check in `SharedPreferences`

Saving the result of the license check is useful for checking the license status without calling `.start()` multiple times.

There are two ways to do this:

Define the `SharedPreferences` and the name of the preference where you want to save the result.

```kotlin
piracyChecker {
	saveResultToSharedPreferences(preferences, "valid_license") // Change "valid_license" with your own value
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.saveResultToSharedPreferences(preferences, "valid_license") // Change "valid_license" with your own value
	...
	.start();
```

</details><br>



Define the `SharedPreferences` name and the name of the preference where you want to save the result.

```kotlin
piracyChecker {
	saveResultToSharedPreferences("license_preferences", "valid_license") // Change "license_preferences" and "valid_license" with your own value
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.saveResultToSharedPreferences("license_preferences", "valid_license") // Change "license_preferences" and "valid_license" with your own value
	...
	.start();
```

</details><br>




## Customizations

### Display results in a Dialog or a new Activity

It's recommended to show a new Activity instead of a Dialog when the license is not valid. This way you make sure that the main activity of the app is finished.

By default a non-cancelable Dialog will be displayed.

```kotlin
piracyChecker {
	display(Display.ACTIVITY)
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.display(Display.ACTIVITY)
	...
	.start();
```

</details><br>



By default, the displayed Activity will use the library colors. To apply a custom primary and primary dark color, and to define if the activity should show normal or light status bar, use:

```kotlin
withActivityColors(R.color.colorPrimary, R.color.colorPrimaryDark, withLightStatusBar)
```

You can also define a custom layout xml for this activity content, using:

```kotlin
.withActivityLayout(R.layout.my_custom_layout)
```

### Using custom callbacks
Adding a callback to the builder allows you to customize what will happen when the license has been checked and manage the license check errors if the user is not allowed to use the app. Keep in mind that when using this method **you must be aware of blocking the app from unauthorized users**.

By default, the library will display a non-cancelable dialog if the user is not allowed to use the app, otherwise nothing will happen.

Use the builder and add following:

```kotlin
callback {
    allow {
        // Do something when the user is allowed to use the app
    }
    doNotAllow { piracyCheckerError, pirateApp ->
        // You can either do something specific when the user is not allowed to use the app
        // Or manage the error, using the 'error' parameter, yourself (Check errors at {@link PiracyCheckerError}).
        
        // Additionally, if you enabled the check of pirate apps and/or third-party stores, the 'app' param
        // is the app that has been detected on device. App can be null, and when null, it means no pirate app or store was found,
        // or you disabled the check for those apps.
        // This allows you to let users know the possible reasons why license is been invalid.
    }
    onError { error ->
        // This method is not required to be implemented/overriden but...
        // You can either do something specific when an error occurs while checking the license,
        // Or manage the error, using the 'error' parameter, yourself (Check errors at {@link PiracyCheckerError}).
    }
}
```

<details><summary><b>Java Sample</b></summary>

```Java
.callback(new PiracyCheckerCallback() {
	@Override
	public void allow() {
		// Do something when the user is allowed to use the app
	}
	
	@Override
	public void doNotAllow(@NonNull PiracyCheckerError error, @Nullable PirateApp app) {
		// You can either do something specific when the user is not allowed to use the app
		// Or manage the error, using the 'error' parameter, yourself (Check errors at {@link PiracyCheckerError}).
		
		// Additionally, if you enabled the check of pirate apps and/or third-party stores, the 'app' param
		// is the app that has been detected on device. App can be null, and when null, it means no pirate app or store was found,
		// or you disabled the check for those apps.
		// This allows you to let users know the possible reasons why license is been invalid.
	}

	@Override
	public void onError(@NonNull PiracyCheckerError error) {
		// This method is not required to be implemented/overridden but...
		// You can either do something specific when an error occurs while checking the license,
		// Or manage the error, using the 'error' parameter, yourself (Check errors at {@link PiracyCheckerError}).
    }
})
```

</details><br>



## FAQs
#### Can I protect my app using more than one validation method?
Sure. You can use as many validation methods in the builder as you want. For example:

```kotlin
piracyChecker {
	enableGooglePlayLicensing("BASE_64_LICENSE_KEY")
	enableSigningCertificates("YOUR_APK_SIGNATURE")
	enableUnauthorizedAppsCheck()
	saveResultToSharedPreferences("my_app_preferences", "valid_license")
	...
}.start()
```

<details><summary><b>Java Sample</b></summary>

```java
new PiracyChecker(this)
	.enableGooglePlayLicensing("BASE_64_LICENSE_KEY")
	.enableSigningCertificates("YOUR_APK_SIGNATURE")
	.enableUnauthorizedAppsCheck()
	.saveResultToSharedPreferences("my_app_preferences", "valid_license")
	...
	.start();
```

</details><br>



## License
	Copyright 2018 Javier Santos
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	   http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
