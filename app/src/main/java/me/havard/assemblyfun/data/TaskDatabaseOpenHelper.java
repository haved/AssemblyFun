package me.havard.assemblyfun.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.havard.assemblyfun.data.tables.TaskIDTable;
import me.havard.assemblyfun.data.tables.LocalTaskTable;
import me.havard.assemblyfun.data.tables.Table;
import me.havard.assemblyfun.data.tables.TaskScoreboardTable;
import me.havard.assemblyfun.data.tables.TaskinfoTable;

import static me.havard.assemblyfun.data.AFDatabaseInteractionHelper.*;

/** No longer a default file template ;)
 * Created by Havard on 11/09/2015.
 */
public class TaskDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "assemblyFunDB";

    public TaskDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Table t:getTables()){
            db.execSQL(t.getCreateString());
        }
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
        Log.d("Assembly Fun", "Dropped all known tables");
        for(Table t:getTables())
            db.execSQL("DROP TABLE IF EXISTS " + t.getTableName());
        onCreate(db);
    }

    public void addDefaultValues(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        TaskIDTable.addRow(db, values, 10004444);
        values.clear();

        long task1 = addTaskInfoToTables(db, values, "Tutorial task 1: Running", "Running your application", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.4f, "Assembly Fun", true, false, false, 11111111);
        values.clear();
        addTaskInfoToTables(db, values, "Tutorial task 2: Returning", "Returning the number 10", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.7f, "Assembly Fun", true, false, false, 22002222);
        values.clear();
        addTaskInfoToTables(db, values, "Tutorial task 3: Adding", "Adding two numbers", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.1f, "Assembly Fun", true, false, true, 30000003);
        values.clear();
        addTaskInfoToTables(db, values, "Tutorial task 4: Breaking", "How to exit instantly", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.8f, "Assembly Fun", true, false, true, 10004444);
        values.clear();
        addTaskInfoToTables(db, values, "Tutorial task 5: Comparing", "Comparing numbers", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.7f, "Assembly Fun", true, false, false, 55555005);
        values.clear();
        addTaskInfoToTables(db, values, "Tutorial task 6: Comparing#2", "Using comparisons", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.2f, "Assembly Fun", true, false, true, 100666);
        values.clear();
        addTaskInfoToTables(db, values, "Tutorial task 7: Test", "r0 = max(r1,r0)", System.currentTimeMillis(), Difficulty.VERY_EASY, 4.4f, "Assembly Fun", true, false, true, 0);
        values.clear();
        addTaskInfoToTables(db, values, "Tutorial task 8: Test2", "r0=r0>r1?r0-r1:r0", System.currentTimeMillis(), Difficulty.VERY_EASY, 4.6f, "Assembly Fun", true, false, false, 88887888);
        values.clear();

        registerLocalTaskToDB(db, values, task1, "Hit the green button to run your program. A series of tests are run to check if your program performs to specification.",
                LocalTaskTable.makeTaskTestString(new int[][]{{0}, {4}, {-4}, {7}}, new int[][]{{0}, {4}, {-4}, {7}}));
        values.clear();

        long someTask = addTaskInfoToTables(db, values, "SomeTask", "r0=max(r0,r1)*max(r1,r2)", System.currentTimeMillis()-100000000, Difficulty.VERY_EASY, 4.6f, "SomeGuy", false, false, true, 90184527);
        values.clear();
        registerLocalTaskToDB(db, values, someTask, "Make a program that returns max(r0,r1)*max(r1,r2)", "3,2,5=15;7,4,2=28;1,2,3=6");
        values.clear();

        long myTask = addTaskInfoToTables(db, values, "MyTask", "r0=r0+min(r0,r1)", System.currentTimeMillis(), Difficulty.VERY_EASY, 4.6f, "You", false, true, true, 0);
        values.clear();
        registerLocalTaskToDB(db, values, myTask, "Make r0=r0+min(r0,r1)",
                LocalTaskTable.makeTaskTestString(new int[][]{{0, 2}, {4, 5}, {4, 2}, {7, 3}}, new int[][]{{0}, {8}, {6}, {10}}));
    }

    private Table[] tables;
    private Table[] getTables()
    {
        if(tables==null)
            tables = new Table[]{new TaskIDTable(), new TaskinfoTable(), new LocalTaskTable(), new TaskScoreboardTable()};
        return tables;
    }
}
