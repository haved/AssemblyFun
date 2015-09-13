package me.havard.assemblyfun.me.havard.assemblyfun.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.LocalGlobalPairTable;
import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.LocalTaskTable;
import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.SolvedTasksTable;
import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.Table;
import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.TaskinfoTable;

/** No longer a default file template ;)
 * Created by Havard on 11/09/2015.
 */
public class TaskDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "assemblyFunDB";

    Table[] tables;

    public TaskDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tables = new Table[]{new TaskinfoTable(), new LocalTaskTable(), new SolvedTasksTable(), new LocalGlobalPairTable()};
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
        for(Table t:tables){
            db.execSQL("DROP TABLE IF EXISTS " + t.getTableName());
        }
        onCreate(db);
    }

    public void addDefaultValues(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        addTaskToTables(db, values, "Tutorial task 1: Running", "Run your application by pressing the green run button", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.4f, "Haved", true, true, 11111111);
        addTaskToTables(db, values, "Tutorial task 2: Returning", "Return the number 10 by using 'mov r0, #10'\nr0 is a registry, #10 is a number", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.7f, "Haved", true, true, 22002222);
        addTaskToTables(db, values, "Tutorial task 3: Adding", "Use 'add r0,#5,#6' to store 5+6 in r0\nAll registries store numbers", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.1f, "Haved", true, true, 30000003);
        addTaskToTables(db, values, "Tutorial task 4: Breaking", "You can write 'mov pc, lr' to exit!\npc and lr are special registries", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.8f, "Haved", true, false, 10004444);
        addTaskToTables(db, values, "Tutorial task 5: Comparing", "Use 'cmp r0,#5' to compare r0 and 5\nThe comparison is stored in memory", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.7f, "Haved", true, true, 55550055);
        addTaskToTables(db, values, "Tutorial task 6: Comparing#2", "Use 'cmp r1,#5' to compare r0 and 5\n'movlt r0,#7' will move 7 into r0 only if r1 is less than (lt) 5", System.currentTimeMillis(), Difficulty.TUTORIAL, 4.2f, "Haved", true, false, 100666);
        addTaskToTables(db, values, "Tutorial task 7: Test", "Write a program that compares r0 and r1 and stores the biggest value in r0", System.currentTimeMillis(), Difficulty.VERY_EASY, 4.4f, "Haved", true, false, 77744777);
        addTaskToTables(db, values, "Tutorial task 8: Test2", "If r0 is greater than(-gt) r1, subtract (sub) r1 from r0 and store it in r0", System.currentTimeMillis(), Difficulty.VERY_EASY, 4.6f, "Haved", true, false, 8888988);
    }

    private void addTaskToTables(SQLiteDatabase db, ContentValues values, String name, String desc, long date, Difficulty diff, float rating, String author, boolean local, boolean solved, long globalID)
    {
        values.clear();
        long t = TaskinfoTable.addTaskToDB(db, values, name, desc, date, diff, rating, author);
        if(local) {
            values.clear();
            LocalTaskTable.addTaskIdToDB(db, values, t);
        }
        if(solved) {
            values.clear();
            SolvedTasksTable.addSolvedTaskIdToDB(db, values, t);
        }
        if(globalID!=0)
        {
            values.clear();
            LocalGlobalPairTable.addLocalGloblPairToDB(db, values, t, globalID);
        }
    }
}
