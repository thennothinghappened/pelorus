# https://github.com/KevinnZou/compose-webview-multiplatform/blob/main/README.desktop.md#proguard
-keep class org.cef.** { *; }
-keep class kotlinx.coroutines.swing.SwingDispatcherFactory

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