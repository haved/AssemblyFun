package me.havard.assemblyfun.data;

import android.content.Context;
import android.content.SharedPreferences;

/** A static class for accessing the SharedPreferences for this application's settings
 * Created by Havard on 07.10.2015.
 */
public final class SharedPreferencesHelper {
    public static final String SHARED_PREFERENCES_NAME = "assemblyFunSettings";
    public static final String BOOL_ALWAYS_KEEP_TASKS = "alwaysKeepTasks";
    public static final boolean BOOL_ALWAYS_KEEP_TASKS_DEFAULT_VALUE = false;

    public static boolean alwaysKeepTasks(SharedPreferences preferences)
    {
        return preferences.getBoolean(BOOL_ALWAYS_KEEP_TASKS, BOOL_ALWAYS_KEEP_TASKS_DEFAULT_VALUE);
    }

    public static void setShouldAlwaysKeepTasks(SharedPreferences.Editor editor, boolean alwaysKeepTasks)
    {
        editor.putBoolean(BOOL_ALWAYS_KEEP_TASKS, alwaysKeepTasks);
    }

    public static SharedPreferences getPreferences(Context context)
    {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
    }
}
