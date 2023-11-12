# https://github.com/KevinnZou/compose-webview-multiplatform/blob/main/README.desktop.md#proguard
-keep class org.cef.** { *; }
-keep class kotlinx.coroutines.swing.SwingDispatcherFactory