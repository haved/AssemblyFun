package me.havard.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** A table that holds solutions
 * Created by Havard on 05.10.2015.
 */
public class SolutionsTable extends Table {
    public static final String TABLE_NAME = "solutionsTable";
    public static final String _ID = "_id";
    public static final String _ID_TaskIDs = TaskIDTable._ID_TaskIDs;
    public static final String NAME = "name";
    public static final String SOLUTION_TEXT = "solution_text";
    public static final String SOLUTION_QUALITY = "solution_quality";
    public static final String SPEED = "speed";
    public static final String SIZE = "size";
    public static final String MEMUSE = "memuse";

    public static final int QUALITY_FAIL = 0;
    public static final int QUALITY_PERFECT = 1;

    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                _ID, PRIMARY_KEY,
                _ID_TaskIDs, INT,
                NAME, TEXT,
                SOLUTION_TEXT, TEXT,
                SOLUTION_QUALITY, INT,
                SPEED, INT,
                SIZE, INT,
                MEMUSE, INT,
                TaskIDTable.FOREIGN_KEY_ID_TaskIDs);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static long addRow(SQLiteDatabase db, ContentValues values, long ref_id, String name, String solutionText, int quality, int speed, int size, int memuse)
    {
        values.put(_ID_TaskIDs, ref_id);
        values.put(NAME, name);
        values.put(SOLUTION_TEXT, solutionText);
        values.put(SOLUTION_QUALITY, quality);
        values.put(SPEED, speed);
        values.put(SIZE, size);
        values.put(MEMUSE, memuse);
        return db.insert(TABLE_NAME, null, values);
    }

    public static void deleteRow(SQLiteDatabase db, long ref_id)
    {
        db.delete(TABLE_NAME, _ID_TaskIDs+"=?", new String[]{Long.toString(ref_id)});
    }
}
