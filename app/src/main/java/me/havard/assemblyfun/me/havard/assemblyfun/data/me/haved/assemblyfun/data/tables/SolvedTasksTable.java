package me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** A class for the name and column names of the SolvedTasks table.
 * Created by Havard on 13/09/2015.
 */
public class SolvedTasksTable extends Table {
    public static final String TABLE_NAME = "solvedTasks";

    @Override
    public String getCreateString(){
        return getSQLCreate(TABLE_NAME,
                TaskinfoTable.REF_ID, INT,
                TaskinfoTable.FOREIGN_KEY_REF_ID);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static void populateContentValues(ContentValues values, long ref_id)
    {
        values.put(TaskinfoTable.REF_ID, ref_id);
    }

    public static void addSolvedTaskIdToDB(SQLiteDatabase db, ContentValues values, long ref_id)
    {
        populateContentValues(values, ref_id);
        db.insert(TABLE_NAME, null, values);
    }
}
