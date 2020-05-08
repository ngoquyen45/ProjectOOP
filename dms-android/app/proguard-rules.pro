# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidSDK/tools/proguard/proguard-android.txt
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

# Uncomment when debug with ProGuard
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

#Specifies not to ignore non-public library classes
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

#Preverification is irrelevant for the dex compiler and the Dalvik VM, so we can switch it off with the -dontpreverify option.
-dontpreverify

#Keep classes that are referenced on the AndroidManifest
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService

#Maintain java native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

#To maintain custom components names that are used on layouts XML:
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
#-keep class android.support.v7.widget.SearchView { *; }
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#Maintain enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#To keep parcelable classes (to serialize - deserialize objects to sent through Intents)
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Keep the R
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Support libraries
-keepattributes *Annotation*,EnclosingMethod,Signature,InnerClasses
-dontwarn **CompatHoneycomb
-keep class android.support.v4.** { *; }
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }


# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.**
-keep class org.codehaus.** { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
    public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *;
}

# Spring Android
-keepclassmembers public class org.springframework.** {
    public *;
}
-dontwarn org.springframework.**

# OkHttp
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn retrofit.**
-dontwarn retrofit.appengine.UrlFetchClient
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# Model classes
-keep public class com.viettel.dmsplus.sdk.network.IsoDateSerializer
-keep public class com.viettel.dmsplus.sdk.network.IsoDateDeserializer
-keep public class com.viettel.dmsplus.sdk.network.IsoShortDateSerializer
-keep public class com.viettel.dmsplus.sdk.network.IsoShortDateDeserializer
-keep public class com.viettel.dmsplus.sdk.auth.AuthenticationInfo { *; }
-keep public class com.viettel.dmsplus.sdk.auth.OAuthError { *; }
-keep public class com.viettel.dmsplus.sdk.models.** { *; }

# GMS Services
-dontwarn com.google.android.gms.**

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# PhotoViewer
-dontwarn uk.co.senab.photoview.**

# EndlessAdapter (becase we use reflection to access some private field)
-keepclassmembers class com.commonsware.cwac.endless.EndlessAdapter {
    private <fields>;
}

# Rey material
-dontwarn com.rey.material.**

# Baidu map
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**