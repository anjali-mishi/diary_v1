# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Room entities
-keep class com.example.myapplication.data.model.** { *; }

# Keep speech recognizer util
-keep class com.example.myapplication.util.SpeechRecognizerManager { *; }
-keep class android.speech.** { *; }

# Keep Room generated implementations (AppDatabase_Impl, DAOs)
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Database class * { *; }
-keep class com.example.myapplication.data.database.** { *; }
-keep class com.example.myapplication.data.dao.** { *; }