# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Vendetta/Library/Android/sdk/tools/proguard/proguard-android.txt
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

#-dontobfuscate

-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.alburagh.alburagh.Models.** { *; }
-keep class com.alburagh.alburagh.Fragments.ContentFragment.** { *; }

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-dontwarn com.duowan.**
-dontwarn loopj.**
-dontwarn squareup.picasso.**
-dontwarn android.support.**
-dontwarn com.squareup.okhttp.**
-dontwarn github.filippudak.**
-dontwarn desarrollodroide.**
-dontwarn github.ksoichiro.**
-dontwarn kotlin.**

-keep class com.duowan.** { *; }
-keep class com.loopj.** { *; }
-keep class squareup.picasso.** { *; }
-keep class android.support.** { *; }
-keep class github.filippudak.** { *; }
-keep class desarrollodroide.** { *; }
-keep class github.ksoichiro.** { *; }
-keep class kotlin.** { *; }
-keep class com.android.vending.billing
-keepattributes InnerClasses