# Keep Hilt generated components.
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper

# kotlinx.serialization (aktif mulai M1/M2)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
