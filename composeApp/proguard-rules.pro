# https://github.com/KevinnZou/compose-webview-multiplatform/blob/main/README.desktop.md#proguard
-keep class org.cef.** { *; }
-keep class kotlinx.coroutines.swing.SwingDispatcherFactory

# https://arkivanov.github.io/Decompose/extensions/compose/#proguard-rules-for-compose-for-desktop-jvm
-keep class com.arkivanov.decompose.extensions.compose.jetbrains.mainthread.SwingMainThreadChecker

-dontwarn javafx.**
-dontwarn org.eclipse.**

-dontwarn org.slf4j.impl.**

-dontwarn javax.annotation.**
-dontwarn okio.**
-dontwarn okhttp3.**

-dontwarn org.tukaani.xz.**

-dontwarn kotlin.**
-dontwarn org.objectweb.asm.**

-dontwarn java.lang.**

-dontwarn org.apache.commons.compress.**
-dontwarn com.jogamp.**