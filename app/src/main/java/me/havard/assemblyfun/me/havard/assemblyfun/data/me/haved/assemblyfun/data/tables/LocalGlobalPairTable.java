package me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** A class for the name and column names of the localGlobalPairs table.
 * Created by Havard on 13/09/2015.
 */
public class LocalGlobalPairTable extends Table {
    public static final String TABLE_NAME = "localGlobalPairs";
    public static final String GLOBAL_TASK_ID = "GlobalTaskID";
    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                TaskinfoTable.REF_ID, INT,
                GLOBAL_TASK_ID, INT,
                TaskinfoTable.FOREIGN_KEY_REF_ID);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static void populateContentValues(ContentValues values, long ref_id, long globalTaskID)
    {
        values.put(TaskinfoTable.REF_ID, ref_id);
        values.put(GLOBAL_TASK_ID, globalTaskID);
    }

    public static void addLocalGloblPairToDB(SQLiteDatabase db, ContentValues values, long ref_id, long globalTaskID)
    {
        populateContentValues(values, ref_id, globalTaskID);
        db.insert(TABLE_NAME, null, values);
    }
}
