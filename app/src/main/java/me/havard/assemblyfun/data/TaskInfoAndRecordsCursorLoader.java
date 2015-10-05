package me.havard.assemblyfun.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.havard.assemblyfun.data.tables.TaskRecordsTable;
import me.havard.assemblyfun.data.tables.TaskinfoTable;

/**
 * Created by Havard on 24.09.2015.
 * Based off of an example on http://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */
public class TaskInfoAndRecordsCursorLoader extends AsyncTaskLoader<Cursor> {
    private SQLiteOpenHelper mDBHelper;
    private Cursor mCursor;

    private String mRef_id;

    private static final String TASK_INFO_AND_RECORDS_QUERY = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s.* FROM %s, %s WHERE %s.%s = ? AND %s.%s = %s.%s LIMIT 1;",
            TaskinfoTable.NAME, TaskinfoTable.DESC, TaskinfoTable.DIFFICULTY, TaskinfoTable.DATE,
            TaskinfoTable.AUTHOR, TaskinfoTable.RATING, TaskinfoTable.FLAGS, TaskRecordsTable.TABLE_NAME,
            TaskinfoTable.TABLE_NAME, TaskRecordsTable.TABLE_NAME,
            TaskinfoTable.TABLE_NAME, TaskinfoTable._ID_TaskIDs,
            TaskinfoTable.TABLE_NAME, TaskinfoTable._ID_TaskIDs,
            TaskRecordsTable.TABLE_NAME, TaskRecordsTable._ID_TaskIDs);
    private static final String TASK_INFO_ONLY_QUERY = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s=? LIMIT 1;",
            TaskinfoTable.NAME, TaskinfoTable.DESC, TaskinfoTable.DIFFICULTY, TaskinfoTable.DATE,
            TaskinfoTable.AUTHOR, TaskinfoTable.RATING, TaskinfoTable.FLAGS,
            TaskinfoTable.TABLE_NAME,
            TaskinfoTable._ID_TaskIDs);

    public TaskInfoAndRecordsCursorLoader(Context context, SQLiteOpenHelper dbHelper, long ref_id) {
        super(context);
        mDBHelper = dbHelper;
        mRef_id = Long.toString(ref_id);
    }

    @Override
    public Cursor loadInBackground()
    {
        Cursor cursor;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        if(AFDatabaseInteractionHelper.containsRowWithWhereStatement(db, TaskRecordsTable.TABLE_NAME, TaskRecordsTable._ID_TaskIDs, mRef_id)) //Is there a task records row with _id_TaskIDs = ref_id?
            cursor = db.rawQuery(TASK_INFO_AND_RECORDS_QUERY, new String[]{mRef_id});
        else
            cursor = db.rawQuery(TASK_INFO_ONLY_QUERY, new String[]{mRef_id});

        if(cursor!=null)
            cursor.getCount(); /*Don't ask me why. Saw it on stack exchange!
            Probably to make sure the cursor has gone through the hole table.*/

        return cursor;
    }

    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            onReleaseResources(cursor);
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && !oldCursor.isClosed() && oldCursor != cursor) {
            onReleaseResources(oldCursor);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor); //We already have data!
        }
        if (takeContentChanged() || mCursor == null /*||configChange*/) {
            forceLoad(); //There's nothing you can do, now. I force you!
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        super.onCanceled(cursor);
        if (cursor != null && !cursor.isClosed())
            onReleaseResources(cursor);
    }

    @Override
    protected void onReset() {
        super.onReset();

        //Quote: "Ensure the loader is stopped"
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            onReleaseResources(mCursor);
        }
        mCursor = null;
    }

    protected void onReleaseResources(Cursor cursor) {
        cursor.close();
    }
}