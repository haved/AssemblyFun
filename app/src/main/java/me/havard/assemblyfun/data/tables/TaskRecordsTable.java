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
    public static final String PERSONAL_SPEED_REC = "personal_speed_rec";
    public static final String SIZE_REC = "size_rec";
    public static final String SIZE_REC_NAME = "size_rec_name";
    public static final String PERSONAL_SIZE_REC = "personal_size_rec";
    public static final String MEMUSE_REC = "memuse_rec";
    public static final String MEMUSE_REC_NAME = "memuse_rec_name";
    public static final String PERSONAL_MEMUSE_REC = "personal_memuse_rec";
    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                _ID_TaskIDs, PRIMARY_KEY,
                SPEED_REC, TYPE_REEL,
                SPEED_REC_NAME, TYPE_TEXT,
                PERSONAL_SPEED_REC, TYPE_REEL,
                SIZE_REC, TYPE_INT,
                SIZE_REC_NAME, TYPE_TEXT,
                PERSONAL_SIZE_REC, TYPE_INT,
                MEMUSE_REC, TYPE_REEL,
                MEMUSE_REC_NAME, TYPE_TEXT,
                PERSONAL_MEMUSE_REC, TYPE_REEL,
                TaskIDTable.FOREIGN_KEY_ID_TaskIDs);
    }

    @Override
    public String getTableName() { return TABLE_NAME; }

    public static void populateContentValues(ContentValues values, long ref_id, float speed_rec, String speed_rec_name, float your_speed_rec,
                                             int size_rec, String size_rec_name, int your_size_rec, float memuse_rec, String memuse_rec_name, float your_memuse_rec)
    {
        if(ref_id!=-1)
            values.put(_ID_TaskIDs, ref_id);
        if(speed_rec!=-1)
            values.put(SPEED_REC, speed_rec);
        if(speed_rec_name!=null)
            values.put(SPEED_REC_NAME, speed_rec_name);
        if(your_speed_rec!=-1)
            values.put(PERSONAL_SPEED_REC, your_speed_rec);
        if(size_rec!=-1)
            values.put(SIZE_REC, size_rec);
        if(size_rec_name!=null)
            values.put(SIZE_REC_NAME, size_rec_name);
        if(your_size_rec!=-1)
            values.put(PERSONAL_SIZE_REC, your_size_rec);
        if(memuse_rec!=-1)
            values.put(MEMUSE_REC, memuse_rec);
        if(memuse_rec_name!=null)
            values.put(MEMUSE_REC_NAME, memuse_rec_name);
        if(your_memuse_rec!=-1)
            values.put(PERSONAL_MEMUSE_REC, your_memuse_rec);
    }

    public static long addRow(SQLiteDatabase db, ContentValues values, long ref_id, float speed_rec, String speed_rec_name, float your_speed_rec,
                              int size_rec, String size_rec_name, int your_size_rec, float memuse_rec, String memuse_rec_name, float your_memuse_rec)
    {
        populateContentValues(values, ref_id, speed_rec, speed_rec_name, your_speed_rec, size_rec, size_rec_name, your_size_rec, memuse_rec, memuse_rec_name, your_memuse_rec);
        return db.insert(TABLE_NAME, null, values);
    }

    private static final String UPDATE_WHERE_STATEMENT = _ID_TaskIDs+"=?";
    public static long updateRow(SQLiteDatabase db, ContentValues values, long ref_id, float speed_rec, String speed_rec_name, float your_speed_rec,
                              int size_rec, String size_rec_name, int your_size_rec, float memuse_rec, String memuse_rec_name, float your_memuse_rec)
    {
        populateContentValues(values, 0, speed_rec, speed_rec_name, your_speed_rec, size_rec, size_rec_name, your_size_rec, memuse_rec, memuse_rec_name, your_memuse_rec);
        return db.update(TABLE_NAME, values, UPDATE_WHERE_STATEMENT, new String[]{Long.toString(ref_id)});
    }
}
