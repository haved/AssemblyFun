package me.havard.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** A class for the name and column names of the localTasks table.
 * See the TaskTestsTextSpecification for details
 * Created by Havard on 13/09/2015.
 */
public class LocalTaskTable extends Table {
    public static final String TABLE_NAME = "localTasks";
    public static final String _ID_TaskIDs = TaskIDTable._ID_TaskIDs;
    public static final String TASK_TEXT = "TaskText";
    public static final String TASK_TESTS = "TaskTests";

    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                _ID_TaskIDs, PRIMARY_KEY,
                TASK_TEXT, TYPE_TEXT,
                TASK_TESTS, TYPE_TEXT,
                TaskIDTable.FOREIGN_KEY_ID_TaskIDs);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static void addRow(SQLiteDatabase db, ContentValues values, long ref_id, String taskText, String taskTests)
    {
        values.put(_ID_TaskIDs, ref_id);
        values.put(TASK_TEXT, taskText);
        values.put(TASK_TESTS, taskTests);
        db.insert(TABLE_NAME, null, values);
    }

    public static void deleteRow(SQLiteDatabase db, long ref_id)
    {
        db.delete(TABLE_NAME, _ID_TaskIDs+"=?", new String[]{Long.toString(ref_id)});
    }
}
