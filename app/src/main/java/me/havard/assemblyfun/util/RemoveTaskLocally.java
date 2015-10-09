package me.havard.assemblyfun.util;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import me.havard.assemblyfun.data.AFDatabaseInteractionHelper;

/** An AsyncTask for removing
 * Created by Havard on 09.10.2015.
 */
public class RemoveTaskLocally extends AsyncTask<Void, Void, Void> {
    private SQLiteOpenHelper mDB;
    private long mTaskID;
    private AllDoneCounter mAllDoneCounter;
    private int mAllDoneCounterId;

    public RemoveTaskLocally(SQLiteOpenHelper db, long taskID, AllDoneCounter counter) {
        mDB = db;
        mTaskID = taskID;
        mAllDoneCounter = counter;
        if(counter!=null)
            mAllDoneCounterId = counter.addTaskToWaitFor();
    }

    @Override
    protected Void doInBackground(Void... params) {
        AFDatabaseInteractionHelper.removeLocalTaskFromDB(mDB.getWritableDatabase(), AFDatabaseInteractionHelper.getClearedContentValuesInstance(), this.mTaskID);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if(mAllDoneCounter!=null)
            mAllDoneCounter.onTaskFinished(mAllDoneCounterId);
        mAllDoneCounter=null;
    }
}