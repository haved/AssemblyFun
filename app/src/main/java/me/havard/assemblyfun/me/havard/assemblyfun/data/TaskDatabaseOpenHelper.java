package me.havard.assemblyfun.me.havard.assemblyfun.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** No longer a default file template ;)
 * Created by Havard on 11/09/2015.
 */
public class TaskDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "assemblyFunDB";

    public static final String LOCAL_TASKINFO_TABLE = "localTaskInfos";
    public static final String LOCAL_TASKINFO_TABLE_TASK_ID = "TaskID";
    public static final String LOCAL_TASKINFO_TABLE_TASK_NAME = "TaskName";
    public static final String LOCAL_TASKINFO_TABLE_TASK_DESC = "TaskDesc";
    public static final String LOCAL_TASKINFO_TABLE_TASK_DATE = "TaskDate";
    public static final String LOCAL_TASKINFO_TABLE_TASK_DIFFICULTY = "TaskDiff";
    public static final String LOCAL_TASKINFO_TABLE_TASK_RATING = "TaskRating";
    public static final String LOCAL_TASKINFO_TABLE_TASK_AUTHOR = "TaskAuthor";
    public static final String LOCAL_TASKINFO_TABLE_CREATE =
            "CREATE TABLE " + LOCAL_TASKINFO_TABLE + " ( "+LOCAL_TASKINFO_TABLE_TASK_ID+" INT, "+LOCAL_TASKINFO_TABLE_TASK_NAME+" VARCHAR(100), "+LOCAL_TASKINFO_TABLE_TASK_DESC+" varchar(140), "+
                LOCAL_TASKINFO_TABLE_TASK_DATE+" INTEGER, "+LOCAL_TASKINFO_TABLE_TASK_DIFFICULTY+" INT, "+LOCAL_TASKINFO_TABLE_TASK_RATING+" INT, "+LOCAL_TASKINFO_TABLE_TASK_AUTHOR+" TEXT);";

    public static final String LOCAL_TASK_TABLE = "localTasks";
    public static final String LOCAL_TASK_TABLE_TASK_ID = "TaskID";
    public static final String LOCAL_TASK_TABLE_TASK_FILE = "TaskFile";
    public static final String LOCAL_TASK_TABLE_CREATE =
            "CREATE TABLE " + LOCAL_TASK_TABLE + " ( "+LOCAL_TASK_TABLE_TASK_ID+" int, "+LOCAL_TASK_TABLE_TASK_FILE+" BLOB );";

    public static final String SOLVED_TASK_TABLE = "solvedTasks";
    public static final String SOLVED_TASK_TABLE_TASK_ID = "TaskID";
    public static final String SOLVED_TASK_TABLE_CREATE =
            "CREATE TABLE " + SOLVED_TASK_TABLE + " ( "+SOLVED_TASK_TABLE_TASK_ID+" int);";

    public TaskDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LOCAL_TASKINFO_TABLE_CREATE);
        db.execSQL(LOCAL_TASK_TABLE_CREATE);
        db.execSQL(SOLVED_TASK_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LOCAL_TASK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOCAL_TASKINFO_TABLE);
        //Should not drop list of solved tasks.
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {

    }
}
