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
    public static final String FLAGS = "flags";

    public static final int FLAG_LOCAL = 0b1;
    public static final int FLAG_SOLVED = 0b10;
    public static final int FLAG_SELF_PUBLISHED = 0b100;
    public static final int FLAG_FAVOURITE = 0b1000;
    public static final int FLAG_GLOBAL = 0b10000;
    public static final int FLAGS_ALL = FLAG_LOCAL+FLAG_SOLVED+FLAG_SELF_PUBLISHED+FLAG_FAVOURITE+FLAG_GLOBAL;

    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                _ID_TaskIDs, PRIMARY_KEY,
                NAME, TEXT,
                DESC, TEXT,
                DATE, INT,
                DIFFICULTY, INT,
                RATING, REEL,
                AUTHOR, TEXT,
                FLAGS, INT,
                TaskIDTable.FOREIGN_KEY_ID_TaskIDs);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static void populateContentValues(ContentValues values, long ref_id, String name, String desc, long date, Difficulty diff,
                                             float rating, String author, int flags)
    {
        values.put(_ID_TaskIDs, ref_id);
        values.put(NAME, name);
        values.put(DESC, desc);
        values.put(DATE, date);
        values.put(DIFFICULTY, diff.ordinal());
        values.put(RATING, rating);
        values.put(AUTHOR, author);
        values.put(FLAGS, flags);
    }

    public static long addRow(SQLiteDatabase db, ContentValues values, long ref_id, String name, String desc, long date, Difficulty diff,
                                   float rating, String author, int flags) {
        populateContentValues(values, ref_id, name, desc, date, diff, rating, author, flags);
        return db.insert(TABLE_NAME, null, values);
    }

    public static boolean hasFlag(int flags, int FLAG)
    {
        return (flags&FLAG)!=0;
    }

    public static int addFlag(int flags, int flag)
    {
        return flags|flag;
    }

    public static int removeFlag(int flags, int flag)
    {
        return flags&(FLAGS_ALL-flag);
    }

    public static int getFlags(boolean local, boolean solved, boolean self_published, boolean favourite, boolean global)
    {
        return (local?FLAG_LOCAL:0)|(solved?FLAG_SOLVED:0)|(self_published?FLAG_SELF_PUBLISHED:0)|(favourite?FLAG_FAVOURITE:0)|(global?FLAG_GLOBAL:0);
    }
}
