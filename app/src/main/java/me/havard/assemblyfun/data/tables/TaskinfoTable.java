package me.havard.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.havard.assemblyfun.data.Difficulty;

/** A class for the name and column names of the localTaskinfo table.
 * Created by Havard on 13/09/2015.
 */
public class TaskinfoTable extends Table
{
    public static final String TABLE_NAME = "localTaskInfo";
    public static final String _ID_TaskIDs = TaskIDTable._ID_TaskIDs;
    public static final String NAME = "name";
    public static final String DESC = "desc";
    public static final String DATE = "date";
    public static final String DIFFICULTY = "diff";
    public static final String RATING = "rating";
    public static final String AUTHOR = "author";
    public static final String LOCAL = "local";
    public static final String SOLVED = "solved";
    public static final String SELF_PUBLISHED = "self_published";
    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                _ID_TaskIDs, INT,
                NAME, TEXT,
                DESC, TEXT,
                DATE, INT,
                DIFFICULTY, INT,
                RATING, REEL,
                AUTHOR, TEXT,
                LOCAL, INT,
                SOLVED, INT,
                SELF_PUBLISHED, INT,
                TaskIDTable.FOREIGN_KEY_ID_TaskIDs);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static void populateContentValues(ContentValues values, long ref_id, String name, String desc, long date, Difficulty diff,
                                             float rating, String author, boolean local, boolean solved, boolean self_published)
    {
        values.put(_ID_TaskIDs, ref_id);
        values.put(NAME, name);
        values.put(DESC, desc);
        values.put(DATE, date);
        values.put(DIFFICULTY, diff.ordinal());
        values.put(RATING, rating);
        values.put(AUTHOR, author);
        values.put(LOCAL, local?1:0);
        values.put(SOLVED, solved?1:0);
        values.put(SELF_PUBLISHED, self_published?1:0);
    }

    public static long addTaskToDB(SQLiteDatabase db, ContentValues values, long ref_id, String name, String desc, long date, Difficulty diff,
                                   float rating, String author, boolean local, boolean solved, boolean self_published)
    {
        populateContentValues(values, ref_id, name, desc, date, diff, rating, author, local, solved, self_published);
        return db.insert(TABLE_NAME, null, values);
    }
}
