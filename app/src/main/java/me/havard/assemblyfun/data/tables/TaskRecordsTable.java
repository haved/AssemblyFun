package me.havard.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** A class for the name and column names of the localGlobalPairs table.
 * Created by Havard on 13/09/2015.
 */
public class TaskRecordsTable extends Table {
    public static final String TABLE_NAME = "taskRecordsTable";
    public static final String _ID_TaskIDs = TaskIDTable._ID_TaskIDs;
    public static final String SPEED_REC = "speed_rec";
    public static final String SPEED_REC_NAME = "speed_rec_name";
    public static final String YOUR_SPEED_REC = "your_speed_rec";
    public static final String SIZE_REC = "size_rec";
    public static final String SIZE_REC_NAME = "size_rec_name";
    public static final String YOUR_SIZE_REC = "your_size_rec";
    public static final String MEMUSE_REC = "memuse_rec";
    public static final String MEMUSE_REC_NAME = "memuse_rec_name";
    public static final String YOUR_MEMUSE_REC = "your_memuse_rec";
    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                _ID_TaskIDs, INT,
                SPEED_REC, REEL,
                SPEED_REC_NAME, TEXT,
                YOUR_SPEED_REC, REEL,
                SIZE_REC, REEL,
                SIZE_REC_NAME, TEXT,
                YOUR_SIZE_REC, REEL,
                MEMUSE_REC, REEL,
                MEMUSE_REC_NAME, TEXT,
                YOUR_MEMUSE_REC, REEL,
                TaskIDTable.FOREIGN_KEY_ID_TaskIDs);
    }

    @Override
    public String getTableName() { return TABLE_NAME; }

    public static void populateContentValues(ContentValues values, long ref_id, float speed_rec, String speed_rec_name, float your_speed_rec,
                                             float size_rec, String size_rec_name, float your_size_rec, float memuse_rec, String memuse_rec_name, float your_memuse_rec)
    {
        if(ref_id!=0)
            values.put(_ID_TaskIDs, ref_id);
        values.put(SPEED_REC, speed_rec);
        values.put(SPEED_REC_NAME, speed_rec_name);
        values.put(YOUR_SPEED_REC, your_speed_rec);
        values.put(SIZE_REC, size_rec);
        values.put(SIZE_REC_NAME, size_rec_name);
        values.put(YOUR_SIZE_REC, your_size_rec);
        values.put(MEMUSE_REC, memuse_rec);
        values.put(MEMUSE_REC_NAME, memuse_rec_name);
        values.put(YOUR_MEMUSE_REC, your_memuse_rec);
    }

    public static long addRow(SQLiteDatabase db, ContentValues values, long ref_id, float speed_rec, String speed_rec_name, float your_speed_rec,
                              float size_rec, String size_rec_name, float your_size_rec, float memuse_rec, String memuse_rec_name, float your_memuse_rec)
    {
        populateContentValues(values, ref_id, speed_rec, speed_rec_name, your_speed_rec, size_rec, size_rec_name, your_size_rec, memuse_rec, memuse_rec_name, your_memuse_rec);
        return db.insert(TABLE_NAME, null, values);
    }

    private static final String UPDATE_WHERE_STATEMENT = _ID_TaskIDs+"=?";
    public static long updateRow(SQLiteDatabase db, ContentValues values, long ref_id, float speed_rec, String speed_rec_name, float your_speed_rec,
                              float size_rec, String size_rec_name, float your_size_rec, float memuse_rec, String memuse_rec_name, float your_memuse_rec)
    {
        populateContentValues(values, 0, speed_rec, speed_rec_name, your_speed_rec, size_rec, size_rec_name, your_size_rec, memuse_rec, memuse_rec_name, your_memuse_rec);
        return db.update(TABLE_NAME, values, UPDATE_WHERE_STATEMENT, new String[]{Long.toString(ref_id)});
    }
}
