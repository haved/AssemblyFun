package me.havard.assemblyfun.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Havard on 24.09.2015.
 * Based off of an example on http://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */
public class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {
    private SQLiteOpenHelper mDBHelper;
    private Cursor mCursor;

    private String mQuery;
    private String[] mWhereArgs;

    public SQLiteCursorLoader(Context context, SQLiteOpenHelper dbHelper, String query, String[] whereArgs) {
        super(context);
        mDBHelper = dbHelper;

        mQuery = query;
        mWhereArgs = whereArgs;
    }

    @Override
    public Cursor loadInBackground()
    {
        //Cursor cursor = mDBHelper.getReadableDatabase().rawQuery(mQueryText, null);
        Cursor cursor = mDBHelper.getReadableDatabase().rawQuery(mQuery, mWhereArgs);
        if(cursor!=null)
            cursor.getCount(); /*Don't ask me why.
            Probably to make sure the cursor has gone through the hole list.
            Saw it on stack exchange!*/

        return cursor;
    }

    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            //We don't need the cursor. It was reset!
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
        if (takeContentChanged() | mCursor==null) {
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