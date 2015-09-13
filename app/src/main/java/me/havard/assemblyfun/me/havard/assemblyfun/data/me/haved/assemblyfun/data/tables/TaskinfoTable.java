package me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import me.havard.assemblyfun.me.havard.assemblyfun.data.Difficulty;

/** A class for the name and column names of the localTaskinfo table.
 * Created by Havard on 13/09/2015.
 */
public class TaskinfoTable extends Table
{
    public static final String TABLE_NAME = "localTaskInfo";
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String DESC = "desc";
    public static final String DATE = "date";
    public static final String DIFFICULTY = "diff";
    public static final String RATING = "rating";
    public static final String AUTHOR = "author";
    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                _ID, ROW_ID,
                NAME, TEXT,
                DESC, TEXT,
                DATE, INT,
                DIFFICULTY, INT,
                RATING, REEL,
                AUTHOR, TEXT);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static void populateContentValues(ContentValues values, String name, String desc, long date, Difficulty diff, float rating, String author)
    {
        values.put(NAME, name);
        values.put(DESC, desc);
        values.put(DATE, date);
        values.put(DIFFICULTY, diff.ordinal());
        values.put(RATING, rating);
        values.put(AUTHOR, author);
    }

    public static long addTaskToDB(SQLiteDatabase db, ContentValues values, String name, String desc, long date, Difficulty diff, float rating, String author)
    {
        populateContentValues(values, name, desc, date, diff, rating, author);
        return db.insert(TABLE_NAME, null, values);
    }

    public static final String REF_ID = _ID+"_"+TABLE_NAME;
    public static final String FOREIGN_KEY_REF_ID = "FOREIGN KEY("+ REF_ID +") REFERENCES "+ TABLE_NAME +"("+ _ID +")";
}
