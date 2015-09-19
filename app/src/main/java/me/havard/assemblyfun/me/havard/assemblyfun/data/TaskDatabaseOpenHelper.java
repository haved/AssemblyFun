package me.havard.assemblyfun.me.havard.assemblyfun.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.TaskIDTable;
import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.LocalTaskTable;
import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.SolvedTasksTable;
import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.Table;
import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.TaskinfoTable;

/** No longer a default file template ;)
 * Created by Havard on 11/09/2015.
 */
public class TaskDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "assemblyFunDB";

    Table[] tables;

    public TaskDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tables = new Table[]{new TaskinfoTable(), new LocalTaskTable(), new SolvedTasksTable(), new TaskIDTable()};
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Table t:tables){
            String create = t.getCreateString();
            Log.d("Assembly Fun", "execSQL();   "+create);
            db.execSQL(create);
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
        Log.d("Assembly Fun", "Dropped all tables");
        db.execSQL("DROP TABLE IF EXISTS localGlobalPairs");
        for(Table t:tables){
            db.execSQL("DROP TABLE IF EXISTS " + t.getTableName());
        }
        onCreate(db);
    }

    public void addDefaultValues(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        long task1 = addTaskInfoToTables(db, values, "Tutorial task 1: Running", "Running your application", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.4f, "Haved", true, 11111111);
        addTaskInfoToTables(db, values, "Tutorial task 2: Returning", "Returning the number 10", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.7f, "Haved", true, 22002222);
        addTaskInfoToTables(db, values, "Tutorial task 3: Adding", "Adding two numbers", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.1f, "Haved", true, 30000003);
        addTaskInfoToTables(db, values, "Tutorial task 4: Breaking", "How to exit instantly", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.8f, "Haved", false, 10004444);
        addTaskInfoToTables(db, values, "Tutorial task 5: Comparing", "Comparing numbers", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.7f, "Haved", true, 55550055);
        addTaskInfoToTables(db, values, "Tutorial task 6: Comparing#2", "Using comparisons", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.2f, "Haved", false, 100666);
        addTaskInfoToTables(db, values, "Tutorial task 7: Test", "r0 = max(r1,r0)", System.currentTimeMillis(), Difficulty.VERY_EASY, 4.4f, "Haved", false, 77744777);
        addTaskInfoToTables(db, values, "Tutorial task 8: Test2", "r0=r0>r1?r0-r1:r0", System.currentTimeMillis(), Difficulty.VERY_EASY, 4.6f, "Haved", false, 8888988);

        addTaskToLocalTasks(db, values, task1, "Hit the green button to run your program. A series of tests are run to check if your program performs to specification.",
                LocalTaskTable.makeTaskTestString(new int[][]{{0}, {4}, {-4}, {7}}, new int[][]{{0}, {4}, {-4}, {7}}));
    }

    private void addTaskToLocalTasks(SQLiteDatabase db, ContentValues values, long taskInfoId, String taskText, String taskTests)
    {
        values.clear();
        LocalTaskTable.addLocalTaskToDB(db, values, taskInfoId, taskText, taskTests);
    }

    private long addTaskInfoToTables(SQLiteDatabase db, ContentValues values, String name, String desc, long date, Difficulty diff, float rating, String author, boolean solved, long globalID)
    {
        values.clear();
        long taskID;
        if(globalID==0)
            taskID = TaskIDTable.registerIDs(db, values);
        else
            taskID = TaskIDTable.registerIDs(db, values, globalID);
        values.clear();
        TaskinfoTable.addTaskToDB(db, values, taskID, name, desc, date, diff, rating, author);
        if(solved) {
            values.clear();
            SolvedTasksTable.addSolvedTaskIdToDB(db, values, taskID);
        }
        return taskID;
    }
}
