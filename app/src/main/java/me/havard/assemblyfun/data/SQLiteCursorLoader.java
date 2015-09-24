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
    private String mQueryText;
    private Cursor mCursor;

    public SQLiteCursorLoader(Context context, SQLiteOpenHelper dbHelper, String queryText) {
        super(context);
        mDBHelper = dbHelper;
        mQueryText = queryText;
    }

    @Override
    public Cursor loadInBackground()
    {
        Log.d("Assembly Fun", "Stated loading a cursor! " + mQueryText);
        Cursor cursor = mDBHelper.getReadableDatabase().rawQuery(mQueryText, null);

        if(cursor!=null)
            cursor.getCount(); /*Don't ask me why.
            Probably to make sure the cursor has gone through the hole list.
            Saw it on stack exchange!*/

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
            Log.d("Assembly Fun", "We already have data in the onStartLoading()");
            deliverResult(mCursor); //We already have data!
        }
        if (takeContentChanged() || mCursor == null /*||configChange*/) {
            forceLoad(); //There's nothing you can do, now. I force you!
        }
    }

    @Override
    protected void onStopLoading() {
        Log.d("Assembly Fun", "onStopLoading()");
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        super.onCanceled(cursor);
        Log.d("Assembly Fun", "onCanceled() mCursor = " + mCursor +
                ((mCursor != null && !mCursor.isClosed()) ? "Not closed" : "Closed"));
        if (cursor != null && !cursor.isClosed())
            onReleaseResources(cursor);
    }

    @Override
    protected void onReset() {
        Log.d("Assembly Fun", "onReset()");
        super.onReset();

        //Quote: "Ensure the loader is stopped"
        onStopLoading();

        Log.d("Assembly Fun", "onReset() mCursor = " + mCursor +
                ((mCursor!=null&&!mCursor.isClosed())?"Not closed":"Closed"));
        if (mCursor != null && !mCursor.isClosed()) {
            onReleaseResources(mCursor);
        }
        mCursor = null;
    }

    protected void onReleaseResources(Cursor cursor) {
        Log.d("Assembly Fun", "Cursor released!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        cursor.close();
    }
}