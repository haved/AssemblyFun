package me.havard.assemblyfun;

import android.app.Application;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.havard.assemblyfun.me.havard.assemblyfun.data.TaskDatabaseOpenHelper;

public class AssemblyFunApplication extends Application {

    private TaskDatabaseOpenHelper db;

    public TaskDatabaseOpenHelper getDatabase()
    {
        return db;
    }

    public SQLiteDatabase getReadableDatabase()
    {
        return db.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabase()
    {
        return db.getWritableDatabase();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(db!=null)
            db.close();
        db=new TaskDatabaseOpenHelper(this);

        Cursor c = getReadableDatabase().rawQuery("Select * from " + TaskDatabaseOpenHelper.LOCAL_TASK_TABLE, null);
        c.moveToFirst();
        while(!c.isAfterLast())
        {
            Log.d("AssemblyFun", "TaskID " + c.getInt(0) + ", TaskName " + c.getString(1));
            c.moveToNext();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(db!=null)
            db.close();
        db=null;
    }

}