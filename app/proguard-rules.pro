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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.ovidium.comoriod.data.* { *; }
-keep class com.ovidium.comoriod.data.article.* { *; }
-keep class com.ovidium.comoriod.data.authors.* { *; }
-keep class com.ovidium.comoriod.data.autocomplete.* { *; }
-keep class com.ovidium.comoriod.data.books.* { *; }
-keep class com.ovidium.comoriod.data.favorites.* { *; }
-keep class com.ovidium.comoriod.data.markups.* { *; }
-keep class com.ovidium.comoriod.data.recentlyaddedbooks.* { *; }
-keep class com.ovidium.comoriod.data.recommended.* { *; }
-keep class com.ovidium.comoriod.data.search.* { *; }
-keep class com.ovidium.comoriod.data.titles.* { *; }
-keep class com.ovidium.comoriod.data.trending.* { *; }
-keep class com.ovidium.comoriod.data.types.* { *; }
-keep class com.ovidium.comoriod.data.volumes.* { *; }
-keep class com.ovidium.comoriod.model.* { *; }
-printusage r8-usage.txt