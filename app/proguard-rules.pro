# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
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

##---------------Begin: proguard configuration for MQTT  ----------------------
-dontwarn org.fusesource.**
##---------------End: proguard configuration for MQTT  ------------------------

##---------------Begin: proguard configuration for GreenDAO  ------------------
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}

-keep class **$Properties
##---------------End: proguard configuration for GreenDAO  --------------------

##---------------Begin: proguard configuration for Gson  ----------------------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.foxconn.cnsbg.escort.subsys.communication.** { *; }
-keep class com.foxconn.cnsbg.escort.subsys.location.** { *; }
-keep class com.foxconn.cnsbg.escort.subsys.updater.** { *; }
##---------------End: proguard configuration for Gson  ------------------------
