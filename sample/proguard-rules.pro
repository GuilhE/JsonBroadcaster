# The Mega Proguard!

#sometimes this may be needed (if no JsonAdapters are created, for instance, if you use Gson instead of Moshi with @JsonClass(generateAdapter = true))
#-keep class <project path>.data.local.**{*;}
#-keep class <project path>.data.remote.models.**{*;}

-keepclassmembers enum * { *; }

########################################################
# Firebase
-dontwarn com.crashlytics.**
-keep class com.crashlytics.** { *; }
-keep public class * extends java.lang.Exception
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

########################################################
#Google
-keep class android.arch.** { *; }

##Encrypted Prefs
-keep class com.google.crypto.tink.** { *; }

##Google Maps
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  public static final *** CREATOR;
}

-keep @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <methods>;
}

-keep @interface com.google.android.gms.common.annotation.KeepName
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
  @com.google.android.gms.common.annotation.KeepName *;
}

-keep @interface com.google.android.gms.common.util.DynamiteApi
-keep public @com.google.android.gms.common.util.DynamiteApi class * {
  public <fields>;
  public <methods>;
}

-dontwarn android.security.NetworkSecurityPolicy

########################################################
# Joda Time
-keep class org.joda.**{*;}
-dontwarn org.joda.**

########################################################
# OkHttp3

-dontwarn com.squareup.okhttp.**

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

########################################################
# okio
-dontwarn okio.**
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

########################################################
# Retrofit2
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.Platform$Java8
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

########################################################
# Kotlin coroutines

# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
