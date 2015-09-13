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

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "assemblyFunDB";

    public static final String INT = "INT";
    public static final String REEL = "REEL";
    public static final String TEXT = "TEXT";
    public static final String BLOB = "BLOB";
    public static final String ROW_ID = "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";

    public static final String TASKINFO_TABLE = "localTaskInfo";
    public static final String TASKINFO_TABLE_ID = "_id";
    public static final String TASKINFO_TABLE_TASK_NAME = "name";
    public static final String TASKINFO_TABLE_TASK_DESC = "desc";
    public static final String TASKINFO_TABLE_TASK_DATE = "date";
    public static final String TASKINFO_TABLE_TASK_DIFFICULTY = "diff";
    public static final String TASKINFO_TABLE_TASK_RATING = "rating";
    public static final String TASKINFO_TABLE_TASK_AUTHOR = "author";
    public static final String TASKINFO_TABLE_CREATE = getSQLCreate(TASKINFO_TABLE,
            TASKINFO_TABLE_ID, ROW_ID,
            TASKINFO_TABLE_TASK_NAME, TEXT,
            TASKINFO_TABLE_TASK_DESC, TEXT,
            TASKINFO_TABLE_TASK_DATE, INT,
            TASKINFO_TABLE_TASK_DIFFICULTY, INT,
            TASKINFO_TABLE_TASK_RATING, REEL,
            TASKINFO_TABLE_TASK_AUTHOR, TEXT);

    public static final String REF_TASKINFO_TABLE_ID = TASKINFO_TABLE_ID+"_"+TASKINFO_TABLE;
    public static final String FOREIGN_KEY_REF_TASKINFO_ID = "FOREIGN KEY("+ REF_TASKINFO_TABLE_ID +") REFERENCES "+ TASKINFO_TABLE +"("+ TASKINFO_TABLE_ID +")";

    public static final String PAIR_LOCAL_GLOBAL_TABLE = "localGlobalPairs";
    public static final String PAIR_TABLE_GLOBAL_TASK_ID = "GlobalTaskID";
    public static final String PAIR_LOCAL_GLOBAL_TABLE_CREATE = getSQLCreate(PAIR_LOCAL_GLOBAL_TABLE,
            REF_TASKINFO_TABLE_ID, INT,
            PAIR_TABLE_GLOBAL_TASK_ID, INT,
            FOREIGN_KEY_REF_TASKINFO_ID);

    public static final String LOCAL_TASK_TABLE = "localTasks";
    public static final String LOCAL_TASK_TABLE_TASK_FILE = "TaskFile";
    public static final String LOCAL_TASK_TABLE_CREATE = getSQLCreate(LOCAL_TASK_TABLE,
            REF_TASKINFO_TABLE_ID, INT,
            LOCAL_TASK_TABLE_TASK_FILE, BLOB,
            FOREIGN_KEY_REF_TASKINFO_ID);

    public static final String SOLVED_TASK_TABLE = "solvedTasks";
    public static final String SOLVED_TASK_TABLE_CREATE = getSQLCreate(SOLVED_TASK_TABLE,
            REF_TASKINFO_TABLE_ID, INT,
            FOREIGN_KEY_REF_TASKINFO_ID);

    public TaskDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Assembly Fun", "execSQL();  " + TASKINFO_TABLE_CREATE);
        db.execSQL(TASKINFO_TABLE_CREATE);
        Log.d("Assembly Fun", "execSQL();  " + LOCAL_TASK_TABLE_CREATE);
        db.execSQL(LOCAL_TASK_TABLE_CREATE);
        Log.d("Assembly Fun", "execSQL();  " + SOLVED_TASK_TABLE_CREATE);
        db.execSQL(SOLVED_TASK_TABLE_CREATE);
        Log.d("Assembly Fun", "execSQL();  " + PAIR_LOCAL_GLOBAL_TABLE_CREATE);
        db.execSQL(PAIR_LOCAL_GLOBAL_TABLE_CREATE);
        addDefaultValues(db);
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
        db.execSQL("DROP TABLE IF EXISTS " + PAIR_LOCAL_GLOBAL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SOLVED_TASK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOCAL_TASK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TASKINFO_TABLE);
        onCreate(db);
    }

    public void addDefaultValues(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(TASKINFO_TABLE_TASK_NAME, "Tutorial task 1: Running");
        values.put(TASKINFO_TABLE_TASK_DESC, "First tutorial task: Learn how to run a program");
        values.put(TASKINFO_TABLE_TASK_DATE, System.currentTimeMillis());
        values.put(TASKINFO_TABLE_TASK_DIFFICULTY, Difficulty.VERY_EASY.ordinal());
        values.put(TASKINFO_TABLE_TASK_RATING, 4.4f);
        values.put(TASKINFO_TABLE_TASK_AUTHOR, "Haved");
        long task1rowId = db.insert(TASKINFO_TABLE, null, values);

        values = new ContentValues(values);
        values.put(TASKINFO_TABLE_TASK_NAME, "Tutorial task 2: Returning");
        values.put(TASKINFO_TABLE_TASK_DESC, "Second tutorial task: Return a the number 10 by storing it in the register r0 using 'mov r0, #10'");
        values.put(TASKINFO_TABLE_TASK_DATE, System.currentTimeMillis());
        values.put(TASKINFO_TABLE_TASK_DIFFICULTY, Difficulty.VERY_EASY.ordinal());
        values.put(TASKINFO_TABLE_TASK_RATING, 4.9f);
        values.put(TASKINFO_TABLE_TASK_AUTHOR, "Haved");
        long task2rowId = db.insert(TASKINFO_TABLE, null, values);

        values = new ContentValues();
        values.put(REF_TASKINFO_TABLE_ID, task1rowId);
        db.insert(SOLVED_TASK_TABLE, null, values);

        values = new ContentValues();
        values.put(REF_TASKINFO_TABLE_ID, task2rowId);
        db.insert(SOLVED_TASK_TABLE, null, values);

        values = new ContentValues();
        values.put(REF_TASKINFO_TABLE_ID, task1rowId);
        db.insert(LOCAL_TASK_TABLE, null, values);

        values = new ContentValues();
        values.put(REF_TASKINFO_TABLE_ID, task1rowId);
        values.put(PAIR_TABLE_GLOBAL_TASK_ID, 12341234);
        db.insert(PAIR_LOCAL_GLOBAL_TABLE, null, values);

        values = new ContentValues();
        values.put(REF_TASKINFO_TABLE_ID, task2rowId);
        values.put(PAIR_TABLE_GLOBAL_TASK_ID, 43211234);
        db.insert(PAIR_LOCAL_GLOBAL_TABLE, null, values);
    }

    private static String getSQLCreate(String name, String... values)
    {
        StringBuilder out = new StringBuilder("CREATE TABLE ");
        out.append(name);
        out.append("( ");
        int i;
        for(i = 0; i+1 < values.length; i+=2)
        {
            out.append(values[i]);
            out.append(" ");
            out.append(values[i+1]);
            if(i+2<values.length)
                out.append(", ");
        }
        if(i<values.length)
            out.append(values[i]);
        out.append(");");

        return out.toString();
    }
}
