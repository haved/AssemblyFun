package me.havard.assemblyfun.util;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import me.havard.assemblyfun.data.AFDatabaseInteractionHelper;

/** An AsyncTask for removing all the data associated with a taskId in a database
 * Created by Havard on 09.10.2015.
 */
public class RemoveAllTaskData extends AsyncTask<Void, Void, Void> {

    private SQLiteOpenHelper mDB;
    private long mTaskID;
    private AllDoneCounter mAllDoneCounter;
    private int mAllDoneCounterId;

    public RemoveAllTaskData(SQLiteOpenHelper db, long taskID, AllDoneCounter counter) {
        mDB = db;
        mTaskID = taskID;
        mAllDoneCounter = counter;
        if(counter!=null)
            mAllDoneCounterId = counter.addTaskToWaitFor();
    }

    @Override
    protected Void doInBackground(Void... params) {
        AFDatabaseInteractionHelper.deleteAllTaskData(mDB.getWritableDatabase(), this.mTaskID);
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
