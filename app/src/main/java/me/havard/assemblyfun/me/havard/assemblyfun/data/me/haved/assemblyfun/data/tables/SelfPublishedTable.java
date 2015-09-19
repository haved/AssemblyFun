package me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** A helper class for the selfPublishedTasks table
 * Created by Havard on 19.09.2015.
 */
public class SelfPublishedTable extends Table {
    public static final String TABLE_NAME = "selfPublishedTasks";

    @Override
    public String getCreateString(){
        return getSQLCreate(TABLE_NAME,
                TaskIDTable._ID_TaskIDs, INT,
                TaskIDTable.FOREIGN_KEY_ID_TaskIDs);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static void populateContentValues(ContentValues values, long ref_id)
    {
        values.put(TaskIDTable._ID_TaskIDs, ref_id);
    }

    public static void registerTaskAsSelfPublished(SQLiteDatabase db, ContentValues values, long ref_id)
    {
        populateContentValues(values, ref_id);
        db.insert(TABLE_NAME, null, values);
    }
}
