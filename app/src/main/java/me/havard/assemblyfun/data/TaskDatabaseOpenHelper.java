package me.havard.assemblyfun.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.havard.assemblyfun.data.tables.SolutionsTable;
import me.havard.assemblyfun.data.tables.TaskIDTable;
import me.havard.assemblyfun.data.tables.LocalTaskTable;
import me.havard.assemblyfun.data.tables.Table;
import me.havard.assemblyfun.data.tables.TaskRecordsTable;
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
        //New task idea: r0=min(r0-min(r0,r1),r1-min(r0,r1))
        ContentValues values = new ContentValues();
        long prevTask = addTaskInfoToTables(db, values, "Tutorial task 1: Running", "Running your application", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.4f, "Assembly Fun", false, false, 1L); values.clear();
        registerLocalTaskToDB(db, values, prevTask, "Hit the green play button to run your program!", "0>0!5>5;4,4>4,4;8>8;7>7:4,5>4,5:10>10:3>3:"); values.clear();
        long prevSolution = addEmptySolution (db, values, prevTask, "Me good solution", "Ay lmao!"); values.clear();
        updateSolutionValues(db, values, prevSolution, prevTask, SolutionsTable.QUALITY_SOLVED, 0, 0, 0); values.clear();
        prevTask = addTaskInfoToTables(db, values, "Tutorial task 2: Returning", "Returning the number 10", System.currentTimeMillis(), Difficulty.TUTORIAL, 3.6f, "Assembly Fun", false, false, 2L); values.clear();
        registerLocalTaskToDB(db, values, prevTask, "Write mov r0,#10 on the first line to move the number 10 into the registry r0. r0 is a registry that can store numbers. " +
                        "The return value of your program is stored here when the program exits. Run the application by hitting the green run button",
                "0>10!5>10;4,4>10;8>10;7,3>10:4,5>10:10>10:3>10:"); values.clear();
        prevTask = addTaskInfoToTables(db, values, "Tutorial task 3: Returning #2", "Returning the registry r1", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.9f, "Assembly Fun", false, false, 3L); values.clear();
        registerLocalTaskToDB(db, values, prevTask, "Write mov r0,r1 on the first line to move the number stored in r1 into the registry r0. r0 and r1 are registries and store numbers. 'mov' stores a number inside the first argument",
                "0,10>10!5,5>5;4,7>7;8,0>0;7,3>3:4,5>5:1,10>10:3,8>8:"); values.clear();
        prevTask = addTaskInfoToTables(db, values, "Tutorial task 4: Adding", "Returning r1+r2", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.5f, "Assembly Fun", false, false, 4L); values.clear();
        registerLocalTaskToDB(db, values, prevTask, "Write add r0,r1,r2 on the first line to add r1 and r2 and store it in r0. add x,y,z means x=y+z",
                "0,10,4>14!5,5,5>10;4,7,-3>4;0,8,0>8;7,3,3>6:4,5,6>11:1,10,3>13:3,8,3>11:"); values.clear();
        addEmptySolution(db, values, prevTask, "Example solution", "add r0,r1,r2"); values.clear();
        prevTask = addTaskInfoToTables(db, values, "Product of biggest", "r0=max(r0,r1)*max(r1,r2)", System.currentTimeMillis(), Difficulty.VERY_EASY, 5f, "Some Guy", false, true, 754647L); values.clear();
        prevSolution = addEmptySolution(db, values, prevTask, "My solution", "cmp r1,r2\nmovgt r2,r1 ;Move r1 to r2 if r1>r2\ncmp r0,r1\nmovgt r1,r0 ;Move r0 to r1 if r0>r1\n mul r0,r1,r2"); values.clear();
        updateSolutionValues(db, values, prevSolution, prevTask, SolutionsTable.QUALITY_SOLVED, 5, 5, 0); values.clear();
        prevTask = addTaskInfoToTables(db, values, "My Task", "r0=r1-min(r0,r1)", System.currentTimeMillis(), Difficulty.VERY_EASY, 2.5f, "You", true, true, 0); values.clear();
        registerLocalTaskToDB(db, values, prevTask, "Make a program that takes in r0 and r1, and returns r1-min(r0,r1) in r0; r0=r1-min(r0,r1)", "5,7>2!4,2>0;8,7>0;5,5>0;3,6>3:"); values.clear();
        addEmptySolution(db, values, prevTask, "My solution", "");
    }

    private Table[] tables;
    private Table[] getTables()
    {
        if(tables==null)
            tables = new Table[]{new TaskIDTable(), new TaskinfoTable(), new LocalTaskTable(), new TaskRecordsTable(), new SolutionsTable()};
        return tables;
    }
}
