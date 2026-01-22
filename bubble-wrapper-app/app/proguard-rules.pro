# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep data classes
-keepclassmembers class xyz.bluefoot.bubblewrapper.PwaConfig { *; }
-keepclassmembers class xyz.bluefoot.bubblewrapper.ui.screens.PublishConfig { *; }

# Solana Mobile Wallet Adapter
-keep class com.solana.mobilewalletadapter.** { *; }
-keep class com.solanamobile.** { *; }

# Network / OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class xyz.bluefoot.bubblewrapper.network.** { *; }

# Bouncy Castle / Crypto
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# BitcoinJ (for base58/crypto utilities)
-keep class org.bitcoinj.** { *; }
-dontwarn org.bitcoinj.**
-dontwarn com.google.common.**

# DataStore
-keep class androidx.datastore.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class xyz.bluefoot.bubblewrapper.**$$serializer { *; }
-keepclassmembers class xyz.bluefoot.bubblewrapper.** {
    *** Companion;
}
-keepclasseswithmembers class xyz.bluefoot.bubblewrapper.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all model classes and data classes
-keep class xyz.bluefoot.bubblewrapper.models.** { *; }
-keep class xyz.bluefoot.bubblewrapper.network.models.** { *; }

# Retrofit / HTTP clients
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# Reflection support for Solana/Web3
-keepattributes RuntimeVisibleAnnotations
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-keep interface kotlin.reflect.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# WebView
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}

# Suppress warnings for optional dependencies
-dontwarn javax.lang.model.element.Modifier
-dontwarn org.conscrypt.**
-dontwarn org.slf4j.**
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

