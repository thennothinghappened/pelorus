-dontwarn java.lang.management.**
-dontwarn org.slf4j.impl.**

# proguard strips this out which makes images not load! :)
-keep class io.kamel.**