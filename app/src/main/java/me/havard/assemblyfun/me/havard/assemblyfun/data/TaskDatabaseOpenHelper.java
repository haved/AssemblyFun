package me.havard.assemblyfun.me.havard.assemblyfun.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** No longer a default file template ;)
 * Created by Havard on 11/09/2015.
 */
public class TaskDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "assemblyFunDB";

    public static final String INT = "INT";
    public static final String ROWID = "ROWID";
    public static final String TEXT = "TEXT";
    public static final String BLOB = "BLOB";

    public static final String LOCAL_GLOBAL_PAIR_TABLE = "localGlobalPairs";
    public static final String PAIR_TABLE_LOCAL_TASK_ID = "LocalTaskID";
    public static final String PAIR_TABLE_GLOBAL_TASK_ID = "GlobalTaskID";
    public static final String LOCAL_GLOBAL_PAIR_TABLE_CREATE = getSQLCreate(LOCAL_GLOBAL_PAIR_TABLE,
            PAIR_TABLE_LOCAL_TASK_ID, ROWID,
            PAIR_TABLE_GLOBAL_TASK_ID, INT);

    public static final String LOCAL_TASKINFO_TABLE = "localTaskInfos";
    public static final String LOCAL_TASKINFO_TABLE_LOCAL_TASK_ID = "LocalTaskID";
    public static final String LOCAL_TASKINFO_TABLE_TASK_NAME = "TaskName";
    public static final String LOCAL_TASKINFO_TABLE_TASK_DESC = "TaskDesc";
    public static final String LOCAL_TASKINFO_TABLE_TASK_DATE = "TaskDate";
    public static final String LOCAL_TASKINFO_TABLE_TASK_DIFFICULTY = "TaskDiff";
    public static final String LOCAL_TASKINFO_TABLE_TASK_RATING = "TaskRating";
    public static final String LOCAL_TASKINFO_TABLE_TASK_AUTHOR = "TaskAuthor";
    public static final String LOCAL_TASKINFO_TABLE_CREATE = getSQLCreate(LOCAL_TASKINFO_TABLE,
                        LOCAL_TASKINFO_TABLE_LOCAL_TASK_ID, ROWID,
                        LOCAL_TASKINFO_TABLE_TASK_NAME, TEXT,
                        LOCAL_TASKINFO_TABLE_TASK_DESC, TEXT,
                        LOCAL_TASKINFO_TABLE_TASK_DATE, INT,
                        LOCAL_TASKINFO_TABLE_TASK_DIFFICULTY, INT,
                        LOCAL_TASKINFO_TABLE_TASK_RATING, INT,
                        LOCAL_TASKINFO_TABLE_TASK_AUTHOR, TEXT);

    public static final String LOCAL_TASK_TABLE = "localTasks";
    public static final String LOCAL_TASK_TABLE_LOCAL_TASK_ID = "LocalTaskID";
    public static final String LOCAL_TASK_TABLE_TASK_FILE = "TaskFile";
    public static final String LOCAL_TASK_TABLE_CREATE = getSQLCreate(LOCAL_TASK_TABLE,
                        LOCAL_TASK_TABLE_LOCAL_TASK_ID, ROWID,
                        LOCAL_TASK_TABLE_TASK_FILE, BLOB);

    public static final String SOLVED_TASK_TABLE = "solvedTasks";
    public static final String SOLVED_TASK_TABLE_LOCAL_TASK_ID = "LocalTaskID";
    public static final String SOLVED_TASK_TABLE_CREATE = getSQLCreate(SOLVED_TASK_TABLE,
                        SOLVED_TASK_TABLE_LOCAL_TASK_ID, ROWID);

    public TaskDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Assembly Fun", "execSQL();  " + LOCAL_GLOBAL_PAIR_TABLE_CREATE);
        db.execSQL(LOCAL_GLOBAL_PAIR_TABLE_CREATE);
        Log.d("Assembly Fun", "execSQL();  " + LOCAL_TASKINFO_TABLE_CREATE);
        db.execSQL(LOCAL_TASKINFO_TABLE_CREATE);
        Log.d("Assembly Fun", "execSQL();  " + LOCAL_TASK_TABLE_CREATE);
        db.execSQL(LOCAL_TASK_TABLE_CREATE);
        Log.d("Assembly Fun", "execSQL();  " + SOLVED_TASK_TABLE_CREATE);
        db.execSQL(SOLVED_TASK_TABLE_CREATE);

        addDefaultValues();
    }

    public void addDefaultValues()
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LOCAL_TASKINFO_TABLE_TASK_NAME, "Tutorial task 1: Running");
        values.put(LOCAL_TASKINFO_TABLE_TASK_DESC, "First tutorial task: Learn how to run a program");
        values.put(LOCAL_TASKINFO_TABLE_TASK_DATE, System.currentTimeMillis());
        values.put(LOCAL_TASKINFO_TABLE_TASK_DIFFICULTY, Difficulty.VERY_EASY.ordinal());
        values.put(LOCAL_TASKINFO_TABLE_TASK_RATING, 1); //TODO: add en enum for ratings
        values.put(LOCAL_TASKINFO_TABLE_TASK_AUTHOR, "Haved");
        db.insert(LOCAL_TASKINFO_TABLE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetAllTables(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {

    }

    public void resetAllTables(SQLiteDatabase db)
    {
        Log.d("Assembly Fun", "Dropped all tables");
        db.execSQL("DROP TABLE IF EXISTS " + SOLVED_TASK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOCAL_TASK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOCAL_TASKINFO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOCAL_GLOBAL_PAIR_TABLE);
        onCreate(db);
    }

    private static String getSQLCreate(String name, String... values)
    {
        StringBuilder out = new StringBuilder("CREATE TABLE ");
        out.append(name);
        out.append("( ");
        for(int i = 0; i+1 < values.length; i+=2)
        {
            out.append(values[i]);
            out.append(" ");
            out.append(values[i+1]);
            if(i+2<values.length)
                out.append(", ");
        }
        out.append(");");

        return out.toString();
    }
}
