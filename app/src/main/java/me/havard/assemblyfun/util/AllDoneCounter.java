package me.havard.assemblyfun.util;

import android.util.Log;

/** A class that keeps track of
 * Created by Havard on 09.10.2015.
 */
public class AllDoneCounter {

    private AllDoneListener mAllDoneListener;
    private int mAddedTasks;
    private int mFinishedTasks;
    private int mCheckSum;
    private int mCheckSumTarget; //The sum of all the task ids.

    public AllDoneCounter(AllDoneListener listener)
    {
        mAllDoneListener = listener;
    }

    public int addTaskToWaitFor()
    {
        mAddedTasks +=1;
        mCheckSumTarget+=mAddedTasks;
        return mAddedTasks; //The first task will be task 1
    }

    public void onTaskFinished(int task)
    {
        if(task > mAddedTasks)
            Log.e("Assembly Fun", "A task with the id " + task + " was done in the AllDoneCounter, but was not added to this AllDoneCounter (unless it was cleared)");
        mCheckSum += task;
        mFinishedTasks++;
        checkIfAllDone();
    }

    /**
     * If the amount of finished tasks is higher or equal to the amount of added tasks, we call AllDoneListener.onAllDone(this);
     */
    public synchronized void checkIfAllDone() {
        if (mFinishedTasks >= mAddedTasks) {
            if (mCheckSum != mCheckSumTarget)
                Log.e("Assembly Fun", "The check sum for the AllDoneCounter didn't match the check sum target. This means the task ids supplied were wrong. Maybe a task has been finished twice?");
            mAllDoneListener.onAllDone(this);
            clear();
        }
    }

    public synchronized boolean isEmpty()
    {
        return mFinishedTasks == 0;
    }

    public synchronized void clear() {
        mAddedTasks = 0;
        mFinishedTasks = 0;
        mCheckSum = 0;
        mCheckSumTarget = 0;
    }

    public interface AllDoneListener{
        void onAllDone(AllDoneCounter counter);
    }
}
