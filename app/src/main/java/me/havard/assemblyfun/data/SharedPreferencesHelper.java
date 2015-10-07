package me.havard.assemblyfun.data;

import android.content.Context;
import android.content.SharedPreferences;

/** A static class for accessing the SharedPreferences for this application's settings
 * Created by Havard on 07.10.2015.
 */
public final class SharedPreferencesHelper {
    public static final String SHARED_PREFERENCES_NAME = "assemblyFunSettings";
    public static final String BOOL_KEEP_UNLISTED_TASKS = "alwaysKeepTasks";
    public static final boolean BOOL_KEEP_UNLISTED_TASKS_DEFAULT_VALUE = false;

    public static boolean shouldKeepUnlistedTasks(SharedPreferences preferences)
    {
        return preferences.getBoolean(BOOL_KEEP_UNLISTED_TASKS, BOOL_KEEP_UNLISTED_TASKS_DEFAULT_VALUE);
    }

    public static void setKeepUnlistedTasks(SharedPreferences.Editor editor, boolean alwaysKeepTasks)
    {
        editor.putBoolean(BOOL_KEEP_UNLISTED_TASKS, alwaysKeepTasks);
    }

    public static SharedPreferences getPreferences(Context context)
    {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
    }
}
