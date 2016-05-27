<h1 align="center">PiracyChecker</h1>
<h4 align="center">Android Library</h4>

<p align="center">
  <a target="_blank" href="https://android-arsenal.com/api?level=8"><img src="https://img.shields.io/badge/API-8%2B-orange.svg"></a>
  <a target="_blank" href="https://travis-ci.org/javiersantos/PiracyChecker"><img src="https://travis-ci.org/javiersantos/PiracyChecker.svg?branch=master"></a>
  <a target="_blank" href="https://www.paypal.me/javiersantos" title="Donate using PayPal"><img src="https://img.shields.io/badge/paypal-donate-yellow.svg" /></a>
  <a target="_blank" href="http://patreon.com/javiersantos" title="Donate using Patreon"><img src="https://img.shields.io/badge/patreon-donate-yellow.svg" /></a>
</p>

<p align="center">An Android library that prevents your app from being pirated / cracked using Google Play Licensing (LVL), APK signature protection and more.</p>

## Disclaimer
This library applies some techniques to help protect your app's users and attempt to thwart reverse engineers and attackers. BUT, this isn't guaranteed stop your app from getting pirated. There is no such thing as 100% security, and a determined and skilled attacker with enougth time could remove these checks from the code. The real objective here is to raise the bar out of reach of opportunist and automatic attackers.

Some of the techniques included in this library can be found [here](https://www.airpair.com/android/posts/adding-tampering-detection-to-your-android-app).


## How to include
Add the repository to your project **build.gradle**:

```Javascript
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```

And add the library to your module **build.gradle**:

```Javascript
dependencies {
    compile 'com.github.javiersantos:PiracyChecker:-SNAPSHOT'
}
```

## Usage
### Verify Google Play Licensing (LVL)
Google Play offers a licensing service that lets you enforce licensing policies for applications that you publish on Google Play. With Google Play Licensing, your application can query Google Play to obtain the licensing status for the current user.

Any application that you publish through Google Play can use the Google Play Licensing service. No special account or registration is needed.

For more information check out the [Google Developers page](https://developer.android.com/google/play/licensing/index.html).
 
```Java
new PiracyChecker(this)
	.enableGooglePlayLicensing("BASE_64_LICENSE_KEY")
	.start();
```

In order to retrieve your BASE64 license key your app must be uploaded to the [Google Play Developer Console](https://play.google.com/apps/publish/). Then access to your app -> Services and APIs.

## Verify your app's signing certificate (signature)
In a nutshell, developers must sign applications with their private key/certificate (contained in a .keystore file) before the app can be installed on user devices. The signing certificate must stay consistent throughout the life of the app, and typically have an expiry date of 25 years in the future.

The app signature will be broken if the .apk is altered in any way — unsigned apps cannot typically be installed. We can imagine an attacker removing license-checking code to enable full app features without paying, for instance. A more dangerous example would be altering the .apk to include malware in a legitimate app to harvest sensitive user data. In order for the altered .apk to be installed, the attacker must resign it.

```Java
new PiracyChecker(this)
	.enableSigningCertificate("478yYkKAQF+KST8y4ATKvHkYibo=") // The original APK signature for the PRODUCTION version
	.start();
```

**BE CAREFUL!!** Your app signature can be retrieved using a PiracyCheckerUtils method. Make sure that you have signed your APK using your production keystore and installed the version that you plan to distribute. Then copy the signature returned by this method on the console and paste in `.enableSigningCertificate("YOUR_APK_SIGNATURE")`

```Java
// This method will print your app signature in the console
Log.e("SIGNATURE", PiracyCheckerUtils.getAPKSignature(this));
```

## Verify the installer
If you only plan to distribute the app on a particular store this technique will block from installing the app using any another store.

Supported stores: Google Play.

```Java
new PiracyChecker(this)
	.enableInstallerId(InstallerID.GOOGLE_PLAY)
	.start();
```

**BE CAREFUL!!** This is a really restrictive technique since it will block your app from being installed using another market or directly installing the .apk on the device. It isn't recommended for most cases.


## Customizations

Use the builder and add following:

```Java
// Provide a custom callback. When using this method you must be aware of blocking the app from unauthorized users.
// Default: The app will display a non-cancelable dialog if the user is not allowed to use the app, otherwise nothing will happen.
.callback(new PiracyCheckerCallback() {
	@Overrirde
	public void allow() {
		// Do something when the user is allowed to use the app
		
	}
	
	@Override
	public void dontAllow() {
		// Do something when the user is not allowed to use the app
		
	}
```

## License
	Copyright 2016 Javier Santos
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	   http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
