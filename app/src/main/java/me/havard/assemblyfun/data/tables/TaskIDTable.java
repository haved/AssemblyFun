package me.havard.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** A class for the name and column names of the localGlobalPairs table.
 * Created by Havard on 13/09/2015.
 */
public class TaskIDTable extends Table {
    public static final String TABLE_NAME = "TaskIDs";
    public static final String _ID = "_id";
    public static final String GLOBAL_TASK_ID = "GlobalTaskID";
    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                _ID, PRIMARY_KEY,
                GLOBAL_TASK_ID, INT);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static void populateContentValues(ContentValues values, long globalTaskID)
    {
        values.put(GLOBAL_TASK_ID, globalTaskID);
    }

    public static long registerIDs(SQLiteDatabase db, ContentValues values, long globalTaskID)
    {
        populateContentValues(values, globalTaskID);
        return db.insert(TABLE_NAME, null, values);
    }

    public static long registerIDs(SQLiteDatabase db, ContentValues values)
    {
        values.put(GLOBAL_TASK_ID, (String)null);
        return db.insert(TABLE_NAME, null, values);
    }

    public static final String _ID_TaskIDs = _ID +"_"+TABLE_NAME;
    public static final String FOREIGN_KEY_ID_TaskIDs = "FOREIGN KEY("+ _ID_TaskIDs +") REFERENCES "+ TABLE_NAME +"("+ _ID +")";
}
