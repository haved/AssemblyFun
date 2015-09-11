package me.havard.assemblyfun.me.havard.assemblyfun.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** No longer a default file template ;)
 * Created by Havard on 11/09/2015.
 */
public class TaskDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "assemblyFunDB";
    public static final String LOCAL_TASK_TABLE = "localTasks";
    public static final String LOCAL_TASK_TABLE_TASK_ID = "TaskID";
    public static final String LOCAL_TASK_TABLE_TASK_NAME = "TaskName";
    public static final String LOCAL_TASK_TABLE_CREATE =
            "CREATE TABLE " + LOCAL_TASK_TABLE + " ( "+LOCAL_TASK_TABLE_TASK_ID+" int, "+LOCAL_TASK_TABLE_TASK_NAME+" varchar(100) );";

    public TaskDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LOCAL_TASK_TABLE_CREATE);
        db.execSQL("INSERT INTO " + LOCAL_TASK_TABLE + " VALUES ('1','The Basics')");
        db.execSQL("INSERT INTO " + LOCAL_TASK_TABLE + " VALUES ('2','Comperasons')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LOCAL_TASK_TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {

    }
}
