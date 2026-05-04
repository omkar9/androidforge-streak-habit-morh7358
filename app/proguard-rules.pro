# Add project specific ProGuard rules here.
# By default, Android Studio generates a proguard-android-optimize.txt file,
# which provides common rules that are good enough for most apps.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Room Persistence Library
-keep class androidx.room.RoomDatabase_Impl{*;}
-keep class com.androidforge.streakhabit.data.local.database.StreakHabitDatabase_Impl{*;}
-keep public class * extends androidx.room.RoomDatabase {
    public <init>(...);
}
-keep class com.androidforge.streakhabit.data.local.database.dao.* { *; }
-keep class com.androidforge.streakhabit.data.local.database.entity.* { *; }
# Keep enums used in TypeConverters for Room
-keep enum com.androidforge.streakhabit.domain.model.FrequencyType { *; }
-keep enum com.androidforge.streakhabit.domain.model.AppTheme { *; }
-keep enum java.time.DayOfWeek { *; }

# Hilt
-keep class dagger.hilt.android.internal.managers.HiltController
-keep class dagger.hilt.android.internal.managers.ActivityComponentManager
-keep class dagger.hilt.android.internal.managers.FragmentComponentManager
-keep class dagger.hilt.android.internal.managers.ViewComponentManager
-keep class dagger.hilt.android.internal.managers.ServiceComponentManager
-keep class dagger.hilt.android.internal.managers.BroadcastReceiverComponentManager
-keep class dagger.hilt.android.internal.builders.* { *; }
-keep class dagger.hilt.android.internal.lifecycle.* { *; }
-keep class dagger.hilt.android.internal.modules.* { *; }
-keep class dagger.hilt.android.internal.testing.* { *; }
-keep class dagger.hilt.android.internal.GeneratedComponentManagerHolder
-keep class dagger.hilt.android.internal.GeneratedComponentManager

# AdMob
-keep class com.google.android.gms.ads.** { *; }
-keep interface com.google.android.gms.ads.** { *; }
-keep public class com.google.android.gms.ads.AdRequest$Builder { *; }
-keep public class com.google.android.gms.ads.AdSize { *; }
-keep class com.google.ads.** { *; }
-keep interface com.google.ads.** { *; }
-keep class com.google.android.gms.internal.ads.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.dynamite.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.** { *; }
-keepnames class kotlinx.coroutines.flow.** { *; }
-keepnames class kotlinx.coroutines.** { *; }