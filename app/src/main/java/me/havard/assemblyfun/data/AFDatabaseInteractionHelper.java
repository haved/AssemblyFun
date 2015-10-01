package me.havard.assemblyfun.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.havard.assemblyfun.data.tables.LocalTaskTable;
import me.havard.assemblyfun.data.tables.TaskIDTable;
import me.havard.assemblyfun.data.tables.TaskinfoTable;

/** A final class that contains methods used when modifying the assembly fun database.
 * Created by Havard on 26.09.2015.
 */
public final class AFDatabaseInteractionHelper
{
    public static void registerLocalTaskToDB(SQLiteDatabase db, ContentValues values, long ref_id, String taskText, String taskTests)
    {
        LocalTaskTable.addRow(db, values, ref_id, taskText, taskTests);
        values.clear();

        values.put(TaskinfoTable.LOCAL, 1);
        db.update(TaskinfoTable.TABLE_NAME, values, TaskinfoTable._ID_TaskIDs + "=?", new String[]{Long.toString(ref_id)});
    }

    public static void removeLocalTaskFromDB(SQLiteDatabase db, ContentValues values, long ref_id)
    {
        LocalTaskTable.deleteRow(db, ref_id);
        if(containsRowWithWhereStatement(db, TaskinfoTable.TABLE_NAME, TaskinfoTable._ID_TaskIDs, Long.toString(ref_id))) {
            values.put(TaskinfoTable.LOCAL, 0);
            db.update(TaskinfoTable.TABLE_NAME, values, TaskinfoTable._ID_TaskIDs + "=?", new String[]{Long.toString(ref_id)});
        }
    }

    public static long addTaskInfoToTables(SQLiteDatabase db, ContentValues values, String name, String desc, long date, Difficulty diff, float rating, String author,
                                           boolean solved, boolean selfPublished, boolean favourite, long globalID)
    {
        long localID=getLocalIDFromGlobalID(db, values, globalID);
        values.clear();

        TaskinfoTable.addRow(db, values, localID, name, desc, date, diff, rating, author, false, solved, selfPublished, favourite);
        return localID;
    }

    public static long getLocalIDFromGlobalID(SQLiteDatabase db, ContentValues values, long globalID)
    {
        if(globalID==0)
            return TaskIDTable.addRow(db, values);
        else{
            Cursor c = db.query(TaskIDTable.TABLE_NAME, new String[]{TaskIDTable._ID, TaskIDTable.GLOBAL_TASK_ID}, TaskIDTable.GLOBAL_TASK_ID+"=?", new String[]{Long.toString(globalID)}, null, null, null, "1");
            long output;
            if(c.isAfterLast())
                output = TaskIDTable.addRow(db, values, globalID);
            else {
                c.moveToFirst();
                output = c.getLong(c.getColumnIndex(TaskIDTable._ID));
            }

            c.close();

            return output;
        }
    }

    public static boolean containsRowWithWhereStatement(SQLiteDatabase db, String tableName, String columnName, String columnValueWanted)
    {
        Cursor c = db.query(tableName, new String[]{columnName}, columnName+"=?", new String[]{columnValueWanted}, null, null, null, "1");
        boolean output = c.getCount() > 0;
        c.close();
        return output;
    }
}
