# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/javiersantos/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Lib
-keep class com.github.javiersantos.**
-dontwarn com.github.javiersantos.**

# LVL
-keep class com.google.**
-keep class autovalue.shaded.com.google.**
-keep class com.android.vending.billing.**
-keep public class com.android.vending.licensing.ILicensingService

-dontwarn org.apache.**
-dontwarn com.google.**
-dontwarn autovalue.shaded.com.google.**
-dontwarn com.android.vending.billing.**